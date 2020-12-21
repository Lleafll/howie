package com.example.howie

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_task.*
import org.w3c.dom.Text
import java.lang.Exception
import java.time.LocalDate

private const val DUE_DATE_ID = 0
private const val SNOOZED_DATE_ID = 1

class TaskActivity : AppCompatActivity(), DatePickerFragment.DatePickerListener {
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
        supportActionBar?.setDisplayShowTitleEnabled(false)
        taskId = intent.getIntExtra("taskId", 0)
        taskLiveData = taskManager.getTask(taskId!!)
        taskLiveData.observe(this, Observer { task ->
            setTask(task)
        })
        snoozeSwitch.setOnCheckedChangeListener { _, isChecked ->
            snoozedTextDate.isVisible = isChecked
        }
        dueSwitch.setOnCheckedChangeListener { _, isChecked ->
            dueTextDate.isVisible = isChecked
        }
        val onClickListenerFactory = { dateId: Int ->
            {view: View ->
                val textView = view as TextView
                val datePicker = DatePickerFragment()
                val arguments = Bundle()
                arguments.putInt("dateId", dateId)
                arguments.putString("date", textView.text.toString())
                datePicker.arguments = arguments
                datePicker.show(supportFragmentManager, "datePicker")
            }
        }
        dueTextDate.setOnClickListener(onClickListenerFactory(DUE_DATE_ID))
        snoozedTextDate.setOnClickListener(onClickListenerFactory(SNOOZED_DATE_ID))
    }

    override fun onDateChanged(id: Int, date: LocalDate) {
        if (id == -1) {
            throw Exception("Pass id arguments to DatePickerFragment")
        }
        if (id == DUE_DATE_ID) {
            dueTextDate.text = date.toString()
        } else if (id == SNOOZED_DATE_ID) {
            snoozedTextDate.text = date.toString()
        }
    }

    private fun setTask(task: Task) {
        taskNameEditText.setText(task.name)
        if (task.importance == Importance.IMPORTANT) {
            importantButton.isChecked = true
        } else {
            unimportantButton.isChecked = true
        }
        setDateFields(dueTextDate, dueSwitch, task.due)
        setDateFields(snoozedTextDate, snoozeSwitch, task.snoozed)
    }

    private fun getTask() = Task(
        taskNameEditText.text.toString(),
        if (importantButton.isChecked) Importance.IMPORTANT else Importance.UNIMPORTANT,
        readDate(dueSwitch, dueTextDate),
        readDate(snoozeSwitch, snoozedTextDate)
    )

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task, menu)
        val saveItem = menu.findItem(R.id.action_save)
        val archiveItem = menu.findItem(R.id.action_archive)
        val unarchiveItem = menu.findItem(R.id.action_unarchive)
        val deleteItem = menu.findItem(R.id.action_delete)
        taskLiveData.observe(this, Observer { task ->
            if (task.archived == null) {
                saveItem.isVisible = true
                archiveItem.isVisible = true
                unarchiveItem.isVisible = false
            } else {
                saveItem.isVisible = false
                archiveItem.isVisible = false
                unarchiveItem.isVisible = true
            }
            deleteItem.isVisible = true
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            taskLiveData.removeObservers(this)
            val task = getTask()
            task.id = taskId!!
            taskManager.update(task)
            finish()
            true
        }
        R.id.action_archive -> {
            taskLiveData.removeObservers(this)
            taskManager.doArchive(taskId!!)
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
}

private fun setDateFields(picker: TextView, switch: SwitchCompat, date: LocalDate?) {
    if (date != null) {
        switch.isChecked = true
        picker.isVisible = true
        picker.text = date.toString()
    } else {
        switch.isChecked = false
        picker.isVisible = false
        picker.text = LocalDate.now().toString()
    }
}

private fun readDate(switch: SwitchCompat, picker: TextView): LocalDate? {
    return if (!switch.isChecked) {
        null
    } else {
        LocalDate.parse(picker.text)
    }
}
