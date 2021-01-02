package com.example.howie

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_widget_configure.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class WidgetConfigureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_configure)
        setResultToCanceled()
        val taskListIds = mutableListOf<Long>()
        configureToolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_ok) {
                lifecycleScope.launch {
                    val repository = WidgetSettingsRepository.getInstance(applicationContext)
                    val selectedIndex = taskListSelection.selectedItemPosition
                    val taskListId = taskListIds[selectedIndex]
                    val widgetId = getAppWidgetId()
                    repository.insert(WidgetSettings(widgetId, taskListId))
                    val widgetSettings: Flow<WidgetSettings?> =
                        repository.getWidgetSettings(widgetId)
                    widgetSettings.collect { updatedWidgetSettings ->
                        if (updatedWidgetSettings != null) {
                            updateWidget(widgetId)
                        }
                    }
                }
                val resultValue = buildIntent()
                setResult(Activity.RESULT_OK, resultValue)
                finish()
                true
            } else {
                super.onOptionsItemSelected(it)
            }
        }
        val taskManager = TaskManager.getInstance(applicationContext)
        taskManager.taskLists.observe(this, Observer {
            val nameList = mutableListOf<String>()
            it.map { taskList -> taskList.name }
            for (taskList in it) {
                nameList.add(taskList.name)
                taskListIds.add(taskList.id)
            }
            val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                nameList
            )
            taskListSelection.adapter = adapter
        })
    }

    private fun updateWidget(appWidgetId: Int) {
        val context: Context = this
        val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
        HowieAppWidgetProvider().onUpdate(context, appWidgetManager, intArrayOf(appWidgetId))
    }

    private fun setResultToCanceled() {
        val resultValue = buildIntent()
        setResult(Activity.RESULT_CANCELED, resultValue)
    }

    private fun buildIntent(): Intent {
        val appWidgetId = getAppWidgetId()
        return Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
    }

    private fun getAppWidgetId(): Int {
        return intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    }
}