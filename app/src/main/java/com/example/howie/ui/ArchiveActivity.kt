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
    val taskAdapter = TaskAdapter {
        val intent = Intent(applicationContext, TaskActivity::class.java)
        intent.putExtra(TASK_ID, it)
        startActivity(intent)
    }
    archive_view.adapter = taskAdapter
    val taskListIndex = intent.getIntExtra(ArchiveActivity.TASKLIST_INDEX, -1)
    if (taskListIndex == -1) {
        error("TaskList index not passed to $this")
    }
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

