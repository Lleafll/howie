package com.example.howie

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBar.DISPLAY_HOME_AS_UP
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_add_task.*
import java.util.*


class AddTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        setSupportActionBar(toolbar)
        supportActionBar?.displayOptions = DISPLAY_HOME_AS_UP
        snoozeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val snoozeButton: Button = findViewById(R.id.snoozeButton)
            snoozeButton.isVisible = isChecked
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_task, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            val returnIntent = Intent()
            val task = Task(
                taskNameEditText.text.toString(),
                Importance.IMPORTANT,
                dueButton.getDate(),
                if (snoozeSwitch.isChecked) snoozeButton.getDate() else null,
                null)
            returnIntent.putExtra("result", task)
            setResult(RESULT_OK, returnIntent)
            finish()
            true
        } else -> {
            super.onOptionsItemSelected(item)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun showDueDatePickerDialog(view: View) {
        val datePicker = DatePickerFragment{ year, month, day ->
            dueButton.setDate(GregorianCalendar(year, month, day))
        }
        datePicker.show(supportFragmentManager, "dueDatePicker")
    }

    @Suppress("UNUSED_PARAMETER")
    fun showSnoozeDatePickerDialog(view: View) {
        val datePicker = DatePickerFragment{ year, month, day ->
            snoozeButton.setDate(GregorianCalendar(year, month, day))
        }
        datePicker.show(supportFragmentManager, "snoozeDatePicker")
    }
}
