package com.example.howie

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_widget_configure.*

class WidgetConfigureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_configure)
        setResultToCanceled()
        configureToolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_ok) {
                updateWidget()
                val resultValue = buildIntent()
                setResult(Activity.RESULT_OK, resultValue)
                finish()
                true
            } else {
                super.onOptionsItemSelected(it)
            }
        }
    }

    private fun updateWidget() {
        val context: Context = this
        val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetId = getAppWidgetId()
        HowieAppWidgetProvider().onUpdate(context,appWidgetManager, intArrayOf(appWidgetId))
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