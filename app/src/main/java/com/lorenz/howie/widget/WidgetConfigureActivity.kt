package com.lorenz.howie.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lorenz.howie.R
import com.lorenz.howie.databinding.ActivityWidgetConfigureBinding

class WidgetConfigureActivity : AppCompatActivity() {
    private val _viewModel: WidgetConfigureViewModel by viewModels {
        WidgetConfigureViewModelFactory(application)
    }
    private lateinit var _binding: ActivityWidgetConfigureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityWidgetConfigureBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setResultToCanceled()
        _binding.configureToolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_ok) {
                val selectedIndex = _binding.taskListSelection.selectedItemPosition
                val widgetId = getAppWidgetId()
                _viewModel.insert(widgetId, selectedIndex)
                val resultValue = buildIntent()
                setResult(Activity.RESULT_OK, resultValue)
                finish()
                true
            } else {
                super.onOptionsItemSelected(it)
            }
        }
        _viewModel.taskListNames.observe(this) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, it)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            _binding.taskListSelection.adapter = adapter
        }
        _viewModel.widgetSettings.observe(this, { updateWidget() })
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