package com.example.howie.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import com.example.howie.R
import com.example.howie.core.Importance
import com.example.howie.core.Schedule
import com.example.howie.core.Task
import com.example.howie.core.scheduleNext
import kotlinx.android.synthetic.main.activity_task.*
import java.time.LocalDate

private const val DUE_DATE_ID = 0
private const val SNOOZED_DATE_ID = 1

class TaskActivity : AppCompatActivity(), DatePickerFragment.DatePickerListener,
    MoveTaskFragment.MoveTaskFragmentListener {

    companion object {
        const val TASK_LIST_INDEX = "currentTaskListId"
        const val TASK_CATEGORY = "task_category"
        const val TASK_ID = "taskId"
    }

    private val viewModel: TaskViewModel by viewModels { TaskViewModelFactory(application) }
    private var taskId: Int? = null
    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val taskListIndex = intent.getIntExtra(TASK_LIST_INDEX, -1)
        if (taskListIndex == -1) {
            error("No ${::TASK_LIST_INDEX.name} passed to $this")
        }
        viewModel.taskList = taskListIndex
        taskId = intent.getIntExtra(TASK_ID, -1)
        if (taskId != -1) {
            viewModel.getTask(taskId!!).observe(this) {
                task = it
                setTask(it)
            }
        } else {
            val task = when (intent.getIntExtra(TASK_CATEGORY, 1)) {
                0 -> Task("", Importance.IMPORTANT, LocalDate.now())
                1 -> Task("")
                2 -> Task("", Importance.UNIMPORTANT, LocalDate.now())
                3 -> Task("", Importance.UNIMPORTANT)
                else -> Task("", Importance.IMPORTANT)
            }
            setTask(task)
            taskNameEditText.requestFocus()
            window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
        snoozeSwitch.setOnCheckedChangeListener { _, isChecked ->
            snoozedTextDate.isVisible = isChecked
        }
        dueSwitch.setOnCheckedChangeListener { _, isChecked ->
            dueTextDate.isVisible = isChecked
        }
        scheduleSwitch.setOnCheckedChangeListener { _, isChecked ->
            schedule_view.isVisible = isChecked
        }
        val onClickListenerFactory = { dateId: Int ->
            { view: View ->
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
        setupColors()
    }

    private fun setupColors() {
        setupActivityColors(resources, window, applicationContext)
    }

    override fun onDateChanged(id: Int, date: LocalDate) {
        if (id == -1) {
            throw Exception("Pass id arguments to DatePickerFragment")
        }
        val dateString = date.toString()
        if (id == DUE_DATE_ID) {
            dueTextDate.text = dateString
        } else if (id == SNOOZED_DATE_ID) {
            snoozedTextDate.text = dateString
            if (date > LocalDate.parse(dueTextDate.text)) {
                // Due can't realistically be before snoozed
                dueTextDate.text = dateString
            }
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
        setScheduleFields(task.schedule, scheduleSwitch, schedule_view)
    }

    private fun buildTaskFromFields() = Task(
        taskNameEditText.text.toString(),
        if (importantButton.isChecked) Importance.IMPORTANT else Importance.UNIMPORTANT,
        readDate(dueSwitch, dueTextDate),
        readDate(snoozeSwitch, snoozedTextDate),
        readSchedule(scheduleSwitch, schedule_view)
    )

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task, menu)
        val saveItem = menu.findItem(R.id.action_save)
        val updateItem = menu.findItem(R.id.action_update)
        val archiveItem = menu.findItem(R.id.action_archive)
        val unarchiveItem = menu.findItem(R.id.action_unarchive)
        val deleteItem = menu.findItem(R.id.action_delete)
        val moveToTaskList = menu.findItem(R.id.action_move_to_different_list)
        val scheduleItem = menu.findItem(R.id.action_schedule)
        if (task == null) {
            saveItem.isVisible = true
            updateItem.isVisible = false
            deleteItem.isVisible = false
            archiveItem.isVisible = false
            unarchiveItem.isVisible = false
            moveToTaskList.isVisible = false
            scheduleItem.isVisible = false
        } else {
            saveItem.isVisible = false
            updateItem.isVisible = true
            deleteItem.isVisible = true
            moveToTaskList.isVisible = true
            scheduleItem.isVisible = true
            if (task!!.archived == null) {
                archiveItem.isVisible = true
                unarchiveItem.isVisible = false
            } else {
                archiveItem.isVisible = false
                unarchiveItem.isVisible = true
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_update -> {
            val task = buildTaskFromFields()
            viewModel.updateTask(task)
            finish()
            true
        }
        R.id.action_save -> {
            val task = buildTaskFromFields()
            viewModel.addTask(task)
            finish()
            true
        }
        R.id.action_archive -> {
            viewModel.doArchive(taskId!!)
            val data = buildIntent(TASK_ARCHIVED_RETURN_CODE)
            data.putExtra(ARCHIVED_TASK_CODE, taskId!!)
            finish()
            true
        }
        R.id.action_unarchive -> {
            viewModel.unarchive(taskId!!)
            finish()
            true
        }
        R.id.action_delete -> {
            viewModel.deleteTask(taskId!!)
            val data = buildIntent(TASK_DELETED_RETURN_CODE)
            data.putExtra(DELETED_TASK_CODE, task)
            finish()
            true
        }
        R.id.action_move_to_different_list -> {
            val dialog = MoveTaskFragment()
            val arguments = Bundle()
            arguments.putInt("taskId", taskId!!)
            dialog.arguments = arguments
            dialog.show(supportFragmentManager, "moveTaskDialog")
            true
        }
        R.id.action_schedule -> {
            val task = buildTaskFromFields()
            if (task.schedule != null) {
                val nextDate = task.schedule.scheduleNext(LocalDate.now())
                val updatedTask = task.copy(snoozed = nextDate, due = nextDate)
                viewModel.updateTask(updatedTask)
                finish()
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun buildIntent(code: Int): Intent {
        val intent = Intent()
        intent.putExtra(TASK_RETURN_CODE, code)
        setResult(RESULT_OK, intent)
        return intent
    }

    override fun onTaskMoved() {
        buildIntent(TASK_MOVED_RETURN_CODE)
        finish()
    }
}

private fun setDateFields(
    picker: TextView, switch: SwitchCompat, date: LocalDate?
) = if (date != null) {
    switch.isChecked = true
    picker.isVisible = true
    picker.text = date.toString()
} else {
    switch.isChecked = false
    picker.isVisible = false
    picker.text = LocalDate.now().toString()
}


private fun readDate(switch: SwitchCompat, picker: TextView): LocalDate? =
    if (!switch.isChecked) {
        null
    } else {
        LocalDate.parse(picker.text)
    }

private fun setScheduleFields(
    schedule: Schedule?,
    switch: SwitchCompat,
    scheduleView: ScheduleView
) {
    if (schedule == null) {
        switch.isChecked = false
    } else {
        switch.isChecked = true
        scheduleView.setSchedule(schedule)
    }
}

private fun readSchedule(
    switch: SwitchCompat,
    scheduleView: ScheduleView
): Schedule? = if (switch.isChecked) {
    scheduleView.getSchedule()
} else {
    null
}
