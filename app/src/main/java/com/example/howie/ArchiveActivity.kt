package com.example.howie

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_archive.*
import kotlinx.android.synthetic.main.activity_archive.toolbar
import kotlinx.android.synthetic.main.fragment_tasks_tab.*

class ArchiveActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        archive_view.layoutManager = LinearLayoutManager(applicationContext)
        val taskAdapter = TaskAdapter {
            val intent = Intent(applicationContext, TaskActivity::class.java)
            intent.putExtra("taskId", it)
            startActivity(intent)
        }
        archive_view.adapter = taskAdapter
        val taskManager = TaskManager.getInstance(applicationContext)
        val tasks = taskManager.archive
        tasks.observe(this, Observer { it.let { taskAdapter.submitList(it) } })
        setupColors()
    }

    private fun setupColors() {
        setupActivityColors(resources, window, applicationContext)
    }
}