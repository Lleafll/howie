package com.example.howie

import android.app.PendingIntent
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

class HowieAppWidgetProvider : AppWidgetProvider() {
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    private lateinit var repository: TasksRepository

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val database = TasksDatabaseSingleton.getDatabase(context)
        repository = TasksRepository(
            database.getTaskDao(),
            database.getTaskListDao(),
            context.getSharedPreferences(HOWIE_SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        )
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

    private fun setupWidget(
        context: Context,
        taskListId: Long,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java).let { intent ->
            intent.putExtra(SHOW_TASK_LIST_EXTRA, taskListId)
            PendingIntent.getActivity(
                context, taskListId.toInt(), intent, 0
            )
        }
        RemoteViews(context.packageName, R.layout.howie_appwidget).apply {
            setOnClickPendingIntent(R.id.background, pendingIntent)
            repository.getTaskCounts(taskListId).observeForever { taskCounts ->
                setTextViewText(R.id.doTextView, toText(taskCounts.doCount))
                setTextViewText(R.id.decideTextView, toText(taskCounts.decideCount))
                setTextViewText(R.id.delegateTextView, toText(taskCounts.delegateCount))
                setTextViewText(R.id.dropTextView, toText(taskCounts.dropCount))
                repository.getTaskList(taskListId).observeForever { taskList ->
                    setTextViewText(R.id.nameTextView, taskList.name)
                    appWidgetManager.updateAppWidget(appWidgetId, this)
                }
            }
        }
    }

    override fun onDisabled(context: Context?) {
        job.cancel()
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val repository = WidgetSettingsRepository.getInstance(context)
        appWidgetIds.forEach { repository.delete(it) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == DATABASE_UPDATE || intent.action == CONFIGURE_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, HowieAppWidgetProvider::class.java)
            )
            onUpdate(context, appWidgetManager, appWidgetIds)
        } else {
            super.onReceive(context, intent)
        }
    }
}

private fun toText(count: Int) = if (count != 0) count.toString() else "✓"