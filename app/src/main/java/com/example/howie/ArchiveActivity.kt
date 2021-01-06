package com.example.howie

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_archive.*

class ArchiveActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels { TaskManagerFactory(application) }

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
        mainViewModel.archive.observe(this, Observer { it.let { taskAdapter.submitList(it) } })
        setupColors()
    }

    private fun setupColors() {
        setupActivityColors(resources, window, applicationContext)
    }
}