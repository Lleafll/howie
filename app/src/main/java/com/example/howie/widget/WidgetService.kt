package com.example.howie.widget

import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import androidx.lifecycle.asLiveData
import com.example.howie.R
import com.example.howie.core.TaskListIndex
import com.example.howie.database.WidgetSettingsDao
import com.example.howie.database.getDatabase
import com.example.howie.ui.MainActivity
import com.example.howie.ui.SHOW_TASK_LIST_EXTRA
import com.example.howie.ui.TasksRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class WidgetService : Service() {
    private lateinit var repository: TasksRepository
    private lateinit var widgetSettingsDao: WidgetSettingsDao

    override fun onStart(intent: Intent?, startId: Int) {
        val context = applicationContext
        val database = getDatabase(context)
        repository = TasksRepository(database.getTaskDao(), database.getTaskListDao())
        widgetSettingsDao = database.getWidgetSettingsDao()
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val componentName = ComponentName(this, HowieAppWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        appWidgetIds.forEach { appWidgetId ->
            widgetSettingsDao.getWidgetSettings(appWidgetId).asLiveData().observeForever {
                if (it != null) {
                    setupWidget(
                        context,
                        TaskListIndex(it.taskListId),
                        appWidgetManager,
                        appWidgetId
                    )
                } else {
                    setupInvalidWidget(context, appWidgetManager, appWidgetId)
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
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
        taskListIndex: TaskListIndex,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java).let { intent ->
            intent.putExtra(SHOW_TASK_LIST_EXTRA, taskListIndex)
            PendingIntent.getActivity(context, taskListIndex.value, intent, 0)
        }
        GlobalScope.launch {
            RemoteViews(context.packageName, R.layout.howie_appwidget).apply {
                setOnClickPendingIntent(R.id.background, pendingIntent)
                repository.getTaskListInformation(taskListIndex).let { taskListInfo ->
                    val taskCounts = taskListInfo.taskCounts
                    setTextViewText(R.id.doTextView, toText(taskCounts.doCount))
                    setTextViewText(R.id.decideTextView, toText(taskCounts.decideCount))
                    setTextViewText(R.id.delegateTextView, toText(taskCounts.delegateCount))
                    setTextViewText(R.id.dropTextView, toText(taskCounts.dropCount))
                    setTextViewText(R.id.nameTextView, taskListInfo.name)
                    appWidgetManager.updateAppWidget(appWidgetId, this)
                }
            }
        }
    }
}

private fun toText(count: Int) = if (count != 0) count.toString() else "✓"