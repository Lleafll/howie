package com.example.howie.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.howie.R
import kotlinx.android.synthetic.main.activity_widget_configure.*

class WidgetConfigureActivity : AppCompatActivity() {
    private val viewModel: WidgetConfigureViewModel by viewModels {
        WidgetConfigureViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_configure)
        setResultToCanceled()
        configureToolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_ok) {
                val selectedIndex = taskListSelection.selectedItemPosition
                val widgetId = getAppWidgetId()
                viewModel.insert(widgetId, selectedIndex)
                val resultValue = buildIntent()
                setResult(Activity.RESULT_OK, resultValue)
                finish()
                true
            } else {
                super.onOptionsItemSelected(it)
            }
        }
        viewModel.taskListNames.observe(this) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, it)
            taskListSelection.adapter = adapter
        }
        viewModel.widgetSettings.observe(this, { updateWidget() })
    }

    private fun updateWidget() {
        val intent = Intent(CONFIGURE_UPDATE, null, this, HowieAppWidgetProvider::class.java)
        sendBroadcast(intent)
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