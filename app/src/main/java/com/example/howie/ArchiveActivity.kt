package com.example.howie

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_archive.*

class ArchiveActivity : AppCompatActivity() {
    private val viewModel: ArchiveViewModel by viewModels { ArchiveViewModelFactory(application) }

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
        viewModel.archive.observe(this, { it.let { taskAdapter.submitList(it) } })
        setupColors()
    }

    private fun setupColors() {
        setupActivityColors(resources, window, applicationContext)
    }
}