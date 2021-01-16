package com.lorenz.howie.ui

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
import com.lorenz.howie.R
import com.lorenz.howie.core.*
import com.lorenz.howie.databinding.ActivityTaskBinding
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
    private lateinit var binding: ActivityTaskBinding
    private var archived: String = ""  // There is no field for this but we need to remember this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val taskListIndex = intent.getParcelableExtra<TaskListIndex>(TASK_LIST_INDEX)!!
        val taskId = intent.getParcelableExtra<TaskIndex>(TASK_ID)
        val taskCategory = intent.getSerializableExtra(TASK_CATEGORY) as TaskCategory?
        viewModel.initialize(taskListIndex, taskId, taskCategory)
        binding.snoozeSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.snoozedTextDate.isVisible = isChecked
        }
        binding.dueSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.dueTextDate.isVisible = isChecked
        }
        binding.scheduleSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.scheduleView.isVisible = isChecked
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
        binding.dueTextDate.setOnClickListener(onClickListenerFactory(DUE_DATE_ID))
        binding.snoozedTextDate.setOnClickListener(onClickListenerFactory(SNOOZED_DATE_ID))
        setupColors()
        viewModel.taskFields.observe(this) { setTask(it) }
        viewModel.finishEvent.observe(this) {
            if (it) {
                finish()
            }
        }
        viewModel.returnTaskDeletedEvent.observe(this) {
            val data = buildIntent(TASK_DELETED_RETURN_CODE)
            data.putExtra(DELETED_TASK_CODE, it)
            finish()
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
            binding.dueTextDate.text = dateString
        } else if (id == SNOOZED_DATE_ID) {
            binding.snoozedTextDate.text = dateString
        }
    }

    private fun setTask(task: TaskFields) {
        binding.taskNameEditText.setText(task.name)
        if (task.name.isEmpty()) {
            binding.taskNameEditText.requestFocus()
            window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
        if (task.importance == Importance.IMPORTANT) {
            binding.importantButton.isChecked = true
        } else {
            binding.unimportantButton.isChecked = true
        }
        setDateFields(binding.dueTextDate, binding.dueSwitch, task.showDue, task.due)
        setDateFields(binding.snoozedTextDate, binding.snoozeSwitch, task.showSnoozed, task.snoozed)
        setScheduleFields(task.schedule, binding.scheduleSwitch, binding.scheduleView)
        archived = task.archived
    }

    private fun buildTaskFromFields() = Task(
        binding.taskNameEditText.text.toString(),
        if (binding.importantButton.isChecked) Importance.IMPORTANT else Importance.UNIMPORTANT,
        readDate(binding.dueSwitch, binding.dueTextDate),
        readDate(binding.snoozeSwitch, binding.snoozedTextDate),
        readSchedule(binding.scheduleSwitch, binding.scheduleView),
        if (archived.isNotEmpty()) LocalDate.parse(archived) else null
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
            viewModel.doArchive()
            val data = buildIntent(TASK_ARCHIVED_RETURN_CODE)
            data.putExtra(ARCHIVED_TASK_CODE, viewModel.taskIndex!!)
            true
        }
        R.id.action_unarchive -> {
            viewModel.unarchive()
            true
        }
        R.id.action_delete -> {
            viewModel.deleteTask()
            true
        }
        R.id.action_move_to_different_list -> {
            val dialog = MoveTaskFragment()
            val arguments = Bundle()
            arguments.putParcelable(MoveTaskFragment.TASK_ID_ARGUMENT, viewModel.taskIndex!!)
            arguments.putParcelable(
                MoveTaskFragment.FROM_TASK_LIST_ARGUMENT,
                viewModel.taskList
            )
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
