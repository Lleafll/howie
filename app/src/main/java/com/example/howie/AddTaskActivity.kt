package com.example.howie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.ActionBar.DISPLAY_HOME_AS_UP
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_add_task.*

class AddTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        setSupportActionBar(toolbar)
        supportActionBar?.displayOptions = DISPLAY_HOME_AS_UP
        val snoozeSwitch: Switch = findViewById(R.id.snoozeSwitch)
        snoozeSwitch.setOnCheckedChangeListener {_, isChecked ->
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
            finish()
            true
        } else -> {
            super.onOptionsItemSelected(item)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun showDueDatePickerDialog(view: View) {
        val datePicker = DatePickerFragment{year, month, day ->
            val dueButton: Button = this.findViewById(R.id.dueButton)
            dueButton.text = toDateString(year, month, day)
        }
        datePicker.show(supportFragmentManager, "dueDatePicker")
    }

    @Suppress("UNUSED_PARAMETER")
    fun showSnoozeDatePickerDialog(view: View) {
        val datePicker = DatePickerFragment{year, month, day ->
            val snoozeButton: Button = this.findViewById(R.id.snoozeButton)
            snoozeButton.text = toDateString(year, month, day)
        }
        datePicker.show(supportFragmentManager, "snoozeDatePicker")
    }
}

private fun toDateString(year: Int, month: Int, day: Int): String {
    return "$day.$month.$year"
}
