package com.example.howie

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.time.LocalDate


private const val LAUNCH_ADD_TASK_ACTIVITY = 1
private const val LAUNCH_TASK_ACTIVITY = 2

class MainActivity : AppCompatActivity() {

    private val taskManager: TaskManager by lazy {
        val dataBase = TasksDatabaseSingleton.getDatabase(applicationContext)
        val repository = TaskRepository(dataBase.getTaskDao())
        TaskManager(repository)
    }
    private val taskAdapter: TaskAdapter by lazy {
        TaskAdapter {
            val intent = Intent(applicationContext, TaskActivity::class.java)
            intent.putExtra("task", it)
            intent.putExtra("taskId", it.id)
            startActivityForResult(intent, LAUNCH_TASK_ACTIVITY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        add_task_button.setOnClickListener {
            val intent = Intent(applicationContext, AddTaskActivity::class.java)
            startActivityForResult(intent, LAUNCH_ADD_TASK_ACTIVITY)
        }
        taskListView.adapter = taskAdapter
        taskManager.tasks.observe(this, Observer { tasks ->
            tasks.let { taskAdapter.submitList(it) }
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
        if (requestCode == LAUNCH_ADD_TASK_ACTIVITY) {
            if (resultCode != RESULT_OK) {
                return
            }
            val task: Task = data!!.getParcelableExtra("result")!!
            taskManager.add(task)
        } else if (requestCode == LAUNCH_TASK_ACTIVITY) {
            if (resultCode != RESULT_OK) {
                return
            }
            val updateTask: Task? = data!!.getParcelableExtra("updateTask")
            if (updateTask != null) {
                updateTask.id = data.getIntExtra("updateTaskId", 0)
                taskManager.update(updateTask)
                return
            }
            val deleteTaskId: Int = data.getIntExtra("deleteTaskId", 0)
            if (deleteTaskId != 0) {
                val task = Task("", Importance.IMPORTANT, LocalDate.now(), null, null)
                task.id = deleteTaskId
                taskManager.delete(task)
                return
            }
        }
    }

}
