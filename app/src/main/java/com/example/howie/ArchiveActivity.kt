package com.example.howie

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_archive.*
import kotlinx.android.synthetic.main.activity_archive.toolbar
import kotlinx.android.synthetic.main.fragment_tasks_tab.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
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
        lifecycleScope.launch {
            tasks.collect {
                taskAdapter.submitList(it)
            }
        }
        setupColors()
    }

    private fun setupColors() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                window.statusBarColor =
                    ContextCompat.getColor(applicationContext, R.color.statusBarColorDark)
                window.navigationBarColor =
                    ContextCompat.getColor(applicationContext, R.color.navigationBarColorDark)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                window.statusBarColor = Color.WHITE
            }
        }
    }
}