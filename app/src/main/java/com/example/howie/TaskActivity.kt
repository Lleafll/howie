package com.example.howie

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_task.*


class TaskActivity : AppCompatActivity() {

    private var oldTask: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        oldTask = intent.getParcelableExtra("task")!!
        fillFields()
        snoozeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val snoozeButton: Button = findViewById(R.id.snoozeButton)
            snoozeButton.isVisible = isChecked
        }
    }

    private fun fillFields() {
        taskNameEditText.setText(oldTask!!.name)
        dueButton.setDate(oldTask!!.due)
        if (oldTask!!.snoozed != null) {
            snoozeSwitch.isChecked = true
            snoozeButton.setDate(oldTask!!.snoozed!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            val returnIntent = Intent()
            val newTask = buildTask()
            returnIntent.putExtra("newTask", newTask)
            returnIntent.putExtra("oldTask", oldTask!!)
            setResult(RESULT_OK, returnIntent)
            finish()
            true
        }
        R.id.action_delete -> {
            val returnIntent = Intent()
            returnIntent.putExtra("oldTask", oldTask!!)
            returnIntent.putExtra("delete", true)
            setResult(RESULT_OK, returnIntent)
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun buildTask() = Task(
        taskNameEditText.text.toString(),
        Importance.IMPORTANT,
        dueButton.getDate(),
        if (snoozeSwitch.isChecked) snoozeButton.getDate() else null,
        null
    )


    @Suppress("UNUSED_PARAMETER")
    fun showDueDatePickerDialog(view: View) {
        val datePicker = DatePickerFragment { date -> dueButton.setDate(date) }
        datePicker.show(supportFragmentManager, "dueDatePicker")
    }

    @Suppress("UNUSED_PARAMETER")
    fun showSnoozeDatePickerDialog(view: View) {
        val datePicker = DatePickerFragment { date -> snoozeButton.setDate(date) }
        datePicker.show(supportFragmentManager, "snoozeDatePicker")
    }
}
