package com.example.howie.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.howie.R
import com.example.howie.core.TaskIndex
import com.example.howie.core.TaskListIndex
import kotlinx.android.synthetic.main.activity_archive.*


class ArchiveActivity : AppCompatActivity() {
    private val viewModel: ArchiveViewModel by viewModels { ArchiveViewModelFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)
        setupToolbar()
        setupArchiveView(viewModel)
        setupActivityColors(resources, window, applicationContext)
    }

    companion object {
        const val TASKLIST_INDEX = "taskListId"
    }
}

private fun ArchiveActivity.setupArchiveView(viewModel: ArchiveViewModel) {
    archive_view.layoutManager = LinearLayoutManager(applicationContext)
    val taskAdapter = TaskAdapter(object : TaskAdapter.Listener {
        override fun onSnoozeToTomorrowClicked(index: TaskIndex) {
            TODO("Implement")
        }

        override fun onRemoveSnoozeClicked(index: TaskIndex) {
            TODO("Implement")
        }

        override fun onRescheduleClicked(index: TaskIndex) {
            TODO("Implement")
        }

        override fun onArchiveClicked(index: TaskIndex) {
            TODO("Implement")
        }

        override fun onUnarchiveClicked(index: TaskIndex) {
            TODO("Implement")
        }

        override fun onEditClicked(index: TaskIndex) {
            val intent = Intent(applicationContext, TaskActivity::class.java)
            intent.putExtra(TaskActivity.TASK_ID, index)
            startActivity(intent)
        }
    })
    archive_view.adapter = taskAdapter
    val taskListIndex: TaskListIndex = intent.getParcelableExtra(ArchiveActivity.TASKLIST_INDEX)!!
    viewModel.setTaskList(taskListIndex)
    viewModel.archive.observe(this, {
        taskAdapter.submitList(it)
    })
}

private fun ArchiveActivity.setupToolbar() {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowTitleEnabled(false)
}

