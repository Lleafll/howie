package com.example.howie.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.example.howie.database.getDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

const val CONFIGURE_UPDATE = "com.example.howie.CONFIGURE_UPDATE"

class HowieAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        context.startService(Intent(context, WidgetService::class.java))
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