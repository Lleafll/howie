package com.lorenz.howie.widget

import android.app.*
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.lifecycle.asLiveData
import com.example.howie.R
import com.lorenz.howie.core.TaskListIndex
import com.lorenz.howie.database.WidgetSettingsDao
import com.lorenz.howie.database.getDatabase
import com.lorenz.howie.ui.MainActivity
import com.lorenz.howie.ui.SHOW_TASK_LIST_EXTRA
import com.lorenz.howie.ui.TasksRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class WidgetService : Service() {
    private lateinit var repository: TasksRepository
    private lateinit var widgetSettingsDao: WidgetSettingsDao

    override fun onCreate() {
        startForegroundWithNotification()
    }

    override fun onDestroy() {
        hideNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundWithNotification()
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
        hideNotification()
        return START_STICKY
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

fun WidgetService.startForegroundWithNotification() {
    val notificationChannelId = "com.example.howie"
    createNotificationChannel(notificationChannelId)
    val notification = buildNotification(notificationChannelId)
    startForeground(1, notification)
}

private fun WidgetService.createNotificationChannel(notificationChannelId: String) {
    val channel = NotificationChannel(
        notificationChannelId,
        "Widget updater background service",
        NotificationManager.IMPORTANCE_NONE
    )
    channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
    val notificationManager: NotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

private fun WidgetService.buildNotification(notificationChannelId: String): Notification {
    val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
        .setContentTitle("App is running in background")
        .setPriority(NotificationManager.IMPORTANCE_MIN)
        .setCategory(Notification.CATEGORY_SERVICE)
        .setOngoing(true)
    return notificationBuilder.build()
}

private fun WidgetService.hideNotification() {
    stopForeground(true)
}