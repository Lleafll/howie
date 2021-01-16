package com.example.howie.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.howie.R
import com.example.howie.core.Task
import com.example.howie.core.TaskIndex
import com.example.howie.core.TaskListIndex
import com.example.howie.databinding.ActivityArchiveBinding
import com.google.android.material.snackbar.Snackbar

class ArchiveActivity : AppCompatActivity() {
    companion object {
        const val TASKLIST_INDEX = "taskListId"
    }

    private val viewModel: ArchiveViewModel by viewModels { ArchiveViewModelFactory(application) }
    private lateinit var binding: ActivityArchiveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArchiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.toolbar)
        setupArchiveView(viewModel, binding.archiveView)
        setupActivityColors(resources, window, applicationContext)
        setupSnackbar(viewModel)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.forceRefresh()
        if (requestCode == TASK_ACTIVITY_REQUEST_CODE) {
            if (data == null) {
                return
            }
            if (resultCode == RESULT_OK) {
                val returnCode = data.getIntExtra(TASK_RETURN_CODE, -1)
                if (returnCode == -1) {
                    error("Supply ${::TASK_RETURN_CODE.name} data when exiting $TaskActivity")
                }
                handleTaskActivityReturn(
                    returnCode,
                    data,
                    findViewById(R.id.archive_coordinator_layout),
                    viewModel
                )
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

private fun handleTaskActivityReturn(
    returnCode: Int, data: Intent, layout: CoordinatorLayout, viewModel: ArchiveViewModel
) {
    when (returnCode) {
        TASK_DELETED_RETURN_CODE -> {
            val task: Task = data.getParcelableExtra(DELETED_TASK_CODE)
                ?: error("Deleted task missing from returned intent")
            viewModel.taskDeletedNotificationEvent.value = task
        }
        TASK_MOVED_RETURN_CODE -> {
            val snackbar = Snackbar.make(layout, "Task Moved", Snackbar.LENGTH_SHORT)
            snackbar.show()
        }
    }
}

private fun ArchiveActivity.setupSnackbar(viewModel: ArchiveViewModel) {
    val layout: CoordinatorLayout = findViewById(R.id.archive_coordinator_layout)
    viewModel.taskUnarchivedNotificationEvent.observe(
        this
    ) { (task, oldDate) ->
        val snackbar = Snackbar.make(layout, "Task unarchived", Snackbar.LENGTH_LONG)
        snackbar.setAction("UNDO") { viewModel.doArchive(task, oldDate) }
        snackbar.show()
    }
    viewModel.taskDeletedNotificationEvent.observe(this) { task ->
        val snackbar = Snackbar.make(layout, "Task deleted", Snackbar.LENGTH_LONG)
        snackbar.setAction("UNDO") { viewModel.addTask(task) }
        snackbar.show()
    }
}

private fun ArchiveActivity.setupArchiveView(
    viewModel: ArchiveViewModel,
    archiveView: RecyclerView
) {
    archiveView.layoutManager = LinearLayoutManager(applicationContext)
    val taskAdapter = TaskAdapter("Archived Tasks", object : TaskAdapter.Listener {
        override fun onSnoozeToTomorrowClicked(index: TaskIndex) {
            // noop
        }

        override fun onRemoveSnoozeClicked(index: TaskIndex) {
            // noop
        }

        override fun onRescheduleClicked(index: TaskIndex) {
            // noop
        }

        override fun onArchiveClicked(index: TaskIndex) {
            // noop
        }

        override fun onUnarchiveClicked(index: TaskIndex) {
            viewModel.unarchive(index)
        }

        override fun onEditClicked(index: TaskIndex) {
            val intent = Intent(applicationContext, TaskActivity::class.java)
            intent.putExtra(TaskActivity.TASK_ID, index)
            intent.putExtra(TaskActivity.TASK_LIST_INDEX, viewModel.currentTaskList)
            startActivityForResult(intent, TASK_ACTIVITY_REQUEST_CODE)
        }
    })
    archiveView.adapter = taskAdapter
    val taskListIndex: TaskListIndex =
        intent.getParcelableExtra(ArchiveActivity.TASKLIST_INDEX)!!
    viewModel.setTaskList(taskListIndex)
    viewModel.archive.observe(this, {
        taskAdapter.submitList(it)
    })
}

private fun ArchiveActivity.setupToolbar(toolbar: androidx.appcompat.widget.Toolbar) {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowTitleEnabled(false)
}

