package com.example.howie

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

const val CONFIGURE_UPDATE = "com.example.howie.CONFIGURE_UPDATE"
const val CONFIGURE_APP_WIDGET_ID = "AppWidgetID"

class HowieAppWidgetProvider : AppWidgetProvider() {
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    private lateinit var taskManager: TaskManager

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        taskManager = TaskManager.getInstance(context)
        val repository = WidgetSettingsRepository.getInstance(context)
        coroutineScope.launch {
            appWidgetIds.forEach { appWidgetId ->
                repository.getWidgetSettings(appWidgetId).collect {
                    if (it != null) {
                        setupWidget(context, it.taskListId, appWidgetManager, appWidgetId)
                    } else {
                        setupInvalidWidget(context, appWidgetManager, appWidgetId)
                    }
                }
            }
        }
    }

    private fun setupInvalidWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        RemoteViews(context.packageName, R.layout.howie_appwidget).apply {
            setTextViewText(R.id.nameTextView, "Invalid Task List")
            appWidgetManager.updateAppWidget(appWidgetId, this)
        }
    }

    private suspend fun setupWidget(
        context: Context,
        taskListId: Long,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        RemoteViews(context.packageName, R.layout.howie_appwidget).apply {
            taskManager.countDoTasks(taskListId).collect { countDoTasks ->
                setTextViewText(R.id.doTextView, toText(countDoTasks))
                taskManager.countDecideTasks(taskListId).collect { countDecideTasks ->
                    setTextViewText(R.id.decideTextView, toText(countDecideTasks))
                    taskManager.countDelegateTasks(taskListId)
                        .collect { countDelegateTasks ->
                            setTextViewText(R.id.delegateTextView, toText(countDelegateTasks))
                            taskManager.countDropTasks(taskListId)
                                .collect { countDropTasks ->
                                    setTextViewText(R.id.dropTextView, toText(countDropTasks))
                                    taskManager.getTaskList(taskListId)
                                        .collect { taskList ->
                                            setTextViewText(R.id.nameTextView, taskList.name)
                                            appWidgetManager.updateAppWidget(appWidgetId, this)
                                        }
                                }
                        }
                }
            }
        }
    }

    override fun onDisabled(context: Context?) {
        job.cancel()
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == CONFIGURE_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val widgetId = intent.getIntExtra(CONFIGURE_APP_WIDGET_ID, -1)
            if (widgetId != -1) {
                onUpdate(context, appWidgetManager, intArrayOf(widgetId))
            }
        } else {
            super.onReceive(context, intent)
        }
    }
}

private fun toText(count: Int) = if (count != 0) count.toString() else "✓"