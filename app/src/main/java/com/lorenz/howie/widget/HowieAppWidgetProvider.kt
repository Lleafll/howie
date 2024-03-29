package com.lorenz.howie.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.lorenz.howie.database.getDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

const val CONFIGURE_UPDATE = "com.lorenz.howie.CONFIGURE_UPDATE"

@DelicateCoroutinesApi
class HowieAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        context.startForegroundService(Intent(context, WidgetService::class.java))
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val database = getDatabase(context)
        val widgetSettingsDao = database.getWidgetSettingsDao()
        appWidgetIds.forEach {
            runBlocking {
                withContext(Dispatchers.IO) {
                    widgetSettingsDao.delete(it)
                }
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == CONFIGURE_UPDATE) {
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