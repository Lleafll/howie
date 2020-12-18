package com.example.howie

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.task_view.view.*


class TaskActivity : AppCompatActivity() {
    private val taskManager: TaskManager by lazy {
        TaskManager.getInstance(applicationContext)
    }
    private var taskId: Int? = null
    private lateinit var taskLiveData: LiveData<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        taskId = intent.getIntExtra("taskId", 0)
        taskLiveData = taskManager.getTask(taskId!!)
        taskLiveData.observe(this, Observer{task ->
            task_view.setTask(task)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            val task = task_view.getTask()
            task.id = taskId!!
            taskManager.update(task)
            finish()
            true
        }
        R.id.action_delete -> {
            taskLiveData.removeObservers(this)
            taskManager.delete(taskId!!)
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
