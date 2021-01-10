package com.example.howie.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.howie.R
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
        override fun onSnoozeToTomorrowClicked(position: Int) {
            TODO("Implement")
        }

        override fun onRemoveSnoozeClicked(position: Int) {
            TODO("Implement")
        }

        override fun onRescheduleClicked(position: Int) {
            TODO("Implement")
        }

        override fun onArchiveClicked(position: Int) {
            TODO("Implement")
        }

        override fun onUnarchiveClicked(position: Int) {
            TODO("Implement")
        }

        override fun onEditClicked(position: Int) {
            val intent = Intent(applicationContext, TaskActivity::class.java)
            intent.putExtra(TaskActivity.TASK_ID, position)
            startActivity(intent)
        }
    })
    archive_view.adapter = taskAdapter
    val taskListIndex = intent.getIntExtra(ArchiveActivity.TASKLIST_INDEX, -1)
    if (taskListIndex == -1) {
        error("TASKLIST_INDEX not passed to $this")
    }
    viewModel.archive.observe(this, {
        taskAdapter.submitList(it)
    })
    viewModel.refreshArchive()
}

private fun ArchiveActivity.setupToolbar() {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowTitleEnabled(false)
}

