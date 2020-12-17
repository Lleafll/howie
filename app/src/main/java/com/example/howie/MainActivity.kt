package com.example.howie

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


private const val LAUNCH_SECOND_ACTIVITY = 1

class MainActivity : AppCompatActivity() {

    private var taskManager: TaskManager? = null
    private var taskAdapter: TaskAdapter? = null

    private fun initializeMembers() {
        taskManager = TaskManager(applicationContext)
        taskAdapter = TaskAdapter {
            val intent = Intent(applicationContext, AddTaskActivity::class.java)
            startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeMembers()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        add_task_button.setOnClickListener {
            val intent = Intent(applicationContext, AddTaskActivity::class.java)
            startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY)
        }
        taskListView.adapter = taskAdapter
        taskManager!!.tasks().observe(this, Observer { tasks ->
            taskAdapter?.setTasks(tasks)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                val task: Task? = data?.getParcelableExtra("result")
                if (task != null) {
                    taskManager!!.add(task)
                    taskAdapter!!.notifyItemInserted(taskAdapter!!.itemCount - 1)
                }
            }
        }
    }

}
