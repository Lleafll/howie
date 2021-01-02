package com.example.howie

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
        coroutineScope.launch {
            appWidgetIds.forEach { appWidgetId ->
                val repository = WidgetSettingsRepository.getInstance(context)
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
        appWidgetId: Int) {
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
}

private fun toText(count: Int) = if (count != 0) count.toString() else "✓"