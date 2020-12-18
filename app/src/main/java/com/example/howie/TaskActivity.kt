package com.example.howie

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_task.*
import java.time.LocalDate


class TaskActivity : AppCompatActivity() {
    private val taskManager: TaskManager by lazy {
        TaskManager.getInstance(applicationContext)
    }
    private var taskId: Int? = null
    private lateinit var taskLiveData: LiveData<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        taskId = intent.getIntExtra("taskId", 0)
        snoozeSwitch.setOnCheckedChangeListener { _, isChecked ->
            snoozeButton.isVisible = isChecked
        }
        dueSwitch.setOnCheckedChangeListener { _, isChecked ->
            dueButton.isVisible = isChecked
        }
        taskLiveData = taskManager.getTask(taskId!!)
        taskLiveData.observe(this, Observer{task ->
            updateFields(task)
        })
    }

    private fun updateFields(task: Task) {
        taskNameEditText.setText(task.name)
        setDateFields(dueButton, dueSwitch, task.due)
        setDateFields(snoozeButton, snoozeSwitch, task.snoozed)
    }

    private fun setDateFields(button: DateButton, switch: SwitchCompat, date: LocalDate?){
        if (date != null) {
            switch.isChecked = true
            button.setDate(date)
        } else {
            switch.isChecked = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            val task = buildTask()
            task.id = taskId!!
            taskManager.update(task)
            finish()
            true
        }
        R.id.action_delete -> {
            taskLiveData.removeObservers(this)
            taskManager.delete(taskId!!)
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
        if (dueSwitch.isChecked) dueButton.getDate() else null,
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
