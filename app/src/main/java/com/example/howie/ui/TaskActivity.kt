package com.example.howie.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import com.example.howie.R
import com.example.howie.core.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val taskListIndex = intent.getParcelableExtra<TaskListIndex>(TASK_LIST_INDEX)!!
        val taskId = intent.getParcelableExtra<TaskIndex>(TASK_ID)
        val taskCategory = intent.getSerializableExtra(TASK_CATEGORY) as TaskCategory?
        viewModel.initialize(taskListIndex, taskId, taskCategory)
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
                arguments.putInt(DatePickerFragment.DATE_ID_ARGUMENT, dateId)
                arguments.putString(DatePickerFragment.DATE_ARGUMENT, textView.text.toString())
                datePicker.arguments = arguments
                datePicker.show(supportFragmentManager, "datePicker")
            }
        }
        dueTextDate.setOnClickListener(onClickListenerFactory(DUE_DATE_ID))
        snoozedTextDate.setOnClickListener(onClickListenerFactory(SNOOZED_DATE_ID))
        setupColors()
        viewModel.taskFields.observe(this) { setTask(it) }
        viewModel.finishEvent.observe(this) {
            if (it) {
                finish()
            }
        }
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

    private fun setTask(task: TaskFields) {
        taskNameEditText.setText(task.name)
        if (task.importance == Importance.IMPORTANT) {
            importantButton.isChecked = true
        } else {
            unimportantButton.isChecked = true
        }
        setDateFields(dueTextDate, dueSwitch, task.showDue, task.due)
        setDateFields(snoozedTextDate, snoozeSwitch, task.showSnoozed, task.snoozed)
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
        viewModel.optionsVisibility.observe(this) {
            saveItem.isVisible = it.save
            updateItem.isVisible = it.update
            deleteItem.isVisible = it.delete
            archiveItem.isVisible = it.archive
            unarchiveItem.isVisible = it.unarchive
            moveToTaskList.isVisible = it.moveToTaskList
            scheduleItem.isVisible = it.schedule
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_update -> {
            val task = buildTaskFromFields()
            viewModel.updateTask(task)
            true
        }
        R.id.action_save -> {
            val task = buildTaskFromFields()
            viewModel.addTask(task)
            true
        }
        R.id.action_archive -> {
            viewModel.doArchive(viewModel.taskIndex!!)
            val data = buildIntent(TASK_ARCHIVED_RETURN_CODE)
            data.putExtra(ARCHIVED_TASK_CODE, viewModel.taskIndex!!)
            true
        }
        R.id.action_unarchive -> {
            viewModel.unarchive(viewModel.taskIndex!!)
            true
        }
        R.id.action_delete -> {
            TODO("Implement")
        }
        R.id.action_move_to_different_list -> {
            val dialog = MoveTaskFragment()
            val arguments = Bundle()
            arguments.putParcelable(MoveTaskFragment.TASK_ID_ARGUMENT, viewModel.taskIndex!!)
            arguments.putParcelable(MoveTaskFragment.FROM_TASK_LIST_ARGUMENT, viewModel.taskList)
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
    picker: TextView,
    switch: SwitchCompat,
    show: Boolean,
    dateString: String
) {
    if (show) {
        switch.isChecked = true
        picker.isVisible = true
    } else {
        switch.isChecked = false
        picker.isVisible = false
    }
    picker.text = dateString
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
