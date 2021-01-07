package com.example.howie.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.lifecycle.asLiveData
import com.example.howie.database.WidgetSettingsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

const val CONFIGURE_UPDATE = "com.example.howie.CONFIGURE_UPDATE"

class HowieAppWidgetProvider : AppWidgetProvider() {
    private lateinit var repository: TasksRepository
    private lateinit var widgetSettingsDao: WidgetSettingsDao

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
        widgetSettingsDao = database.getWidgetSettingsDao()
        appWidgetIds.forEach { appWidgetId ->
            widgetSettingsDao.getWidgetSettings(appWidgetId).asLiveData().observeForever {
                if (it != null) {
                    setupWidget(context, it.taskListId, appWidgetManager, appWidgetId)
                } else {
                    setupInvalidWidget(context, appWidgetManager, appWidgetId)
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

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach {
            runBlocking {
                withContext(Dispatchers.IO) {
                    widgetSettingsDao.delete(it)
                }
            }
        }
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