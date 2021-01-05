package com.example.howie

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.size
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tasks_tab.*

const val SHOW_TASK_LIST_EXTRA = "showTaskList"
const val DATABASE_UPDATE = "com.example.howie.DATABASE_UPDATE"
const val TASK_REQUEST_CODE = 1
const val TASK_RETURN_CODE = "TaskReturnCode"
const val DELETED_TASK_CODE = "DeletedTask"
const val ARCHIVED_TASK_CODE = "ArchivedTask"
const val TASK_DELETED_RETURN_CODE = 0
const val TASK_ARCHIVED_RETURN_CODE = 1
const val TASK_MOVED_RETURN_CODE = 2

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val viewModel: TaskManager by viewModels { TaskManagerFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupTaskButton()
        setupToolBar()
        setupDrawer()
        setupColors()
        switchToIntentTaskList(intent)
        broadcastDatabaseChanges()  // This is hacky but the best way to update the widgets
    }

    private fun setupTaskButton() {
        add_task_button.setOnClickListener {
            val intent = Intent(applicationContext, TaskActivity::class.java)
            intent.putExtra(TASK_CATEGORY, tab_layout.selectedTabPosition)
            startActivity(intent)
        }
    }

    private fun broadcastDatabaseChanges() {
        val taskManager = TaskManager.getInstance(application)
        taskManager.tasks.observe(this, Observer {
            val intent = Intent(DATABASE_UPDATE, null, this, HowieAppWidgetProvider::class.java)
            sendBroadcast(intent)
        })
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        switchToIntentTaskList(intent)
    }

    private fun switchToIntentTaskList(intent: Intent) {
        val taskManager = TaskManager.getInstance(application)
        val taskListId = intent.getLongExtra(SHOW_TASK_LIST_EXTRA, taskManager.currentTaskListId)
        if (taskListId != taskManager.currentTaskListId) {
            val itemId = buildNavigationItemId(taskListId)
            switchTaskList(nav_view.menu.findItem(itemId))
        }
    }

    private fun setupColors() {
        setupActivityColors(resources, window, applicationContext)
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                tab_layout.setBackgroundColor(
                    ContextCompat.getColor(applicationContext, R.color.tabColorDark)
                )
            }
        }
    }

    private fun setupToolBar() {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_show_archive -> {
                    val intent = Intent(applicationContext, ArchiveActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_rename -> {
                    onRenameClick()
                    true
                }
                R.id.action_delete -> {
                    onDeleteClick()
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
        val taskManager = TaskManager.getInstance(application)
        taskManager.currentTaskList.observe(this, Observer {
            toolbar.title = it.name
        })
    }

    private fun onRenameClick() {
        val dialog = RenameTaskListFragment()
        dialog.show(supportFragmentManager, "renameTask")
    }

    private fun onDeleteClick() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setMessage("Delete Task List?")
            .setPositiveButton("Yes") { _, _ ->
                TaskManager.getInstance(application).deleteCurrentTaskList()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun setupDrawer() {
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val taskManager = TaskManager.getInstance(application)
        // TODO: Causes really ugly race conditions
        taskManager.taskLists.observe(this, Observer {
            buildDrawerContent()
        })
        taskManager.tasks.observe(this, Observer { updateDrawerContent() })
    }

    private fun buildDrawerContent() {
        val taskManager = TaskManager.getInstance(application)
        taskManager.taskLists.observeOnce(this, Observer {
            nav_view.menu.removeGroup(R.id.list_groups)
            for (taskList in it) {
                taskManager.getTaskCounts(taskList.id).observe(this, Observer { taskCounts ->
                    val itemId = buildNavigationItemId(taskList.id)
                    val name = buildDrawerItemName(taskList, taskCounts)
                    val item = nav_view.menu.add(R.id.list_groups, itemId, Menu.NONE, name)
                    if (taskManager.currentTaskListId == taskList.id) {
                        item.isChecked = true
                    }
                })
            }
            nav_view.setNavigationItemSelectedListener(this)
        })
    }

    private fun updateDrawerContent() {
        val taskManager = TaskManager.getInstance(application)
        taskManager.taskLists.observeOnce(this, Observer {
            for (taskList in it) {
                taskManager.getTaskCounts(taskList.id).observe(this, Observer { taskCounts ->
                    val itemId = buildNavigationItemId(taskList.id)
                    val item = nav_view.menu.findItem(itemId)
                    if (item != null) {
                        item.title = buildDrawerItemName(taskList, taskCounts)
                    }
                })
            }
        })
    }

    private fun buildNavigationItemId(taskListId: Long) =
        R.id.action_add_list + taskListId.toInt() + 1

    private fun buildDrawerItemName(taskList: TaskList, taskCounts: List<Int>): String {
        return "${taskList.name} (" +
                "${countToString(taskCounts[0])}/" +
                "${countToString(taskCounts[1])}/" +
                "${countToString(taskCounts[2])}/" +
                "${countToString(taskCounts[3])})"
    }


    private fun countToString(count: Int) = when (count) {
        0 -> "✓"
        else -> count.toString()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add_list) {
            val taskManager = TaskManager.getInstance(application)
            taskManager.addTaskList("New Task List")
        } else {
            switchTaskList(item)
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun switchTaskList(item: MenuItem) {
        for (i in 0 until nav_view.menu.size) {
            nav_view.menu.getItem(i).isChecked = false
        }
        item.isChecked = true
        val taskManager = TaskManager.getInstance(application)
        val itemId = item.itemId - R.id.action_add_list - 1
        taskManager.switchToTaskList(itemId.toLong())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TASK_REQUEST_CODE) {
            if (data == null) {
                return
            }
            if (resultCode == RESULT_OK) {
                val returnCode = data.getIntExtra(TASK_RETURN_CODE, -1)
                if (returnCode == -1) {
                    throw Exception("Supply TASK_RETURN_CODE data when exiting TaskActivity")
                }
                handleTaskActivityReturn(returnCode, data)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleTaskActivityReturn(returnCode: Int, data: Intent) {
        val layout = findViewById<CoordinatorLayout>(R.id.main_coordinator_layout)
        val text = when (returnCode) {
            TASK_DELETED_RETURN_CODE -> "Task Deleted"
            TASK_ARCHIVED_RETURN_CODE -> "Task Archived"
            TASK_MOVED_RETURN_CODE -> "Task Moved"
            else -> "NOTIFICATION"
        }
        val duration = when (returnCode) {
            TASK_DELETED_RETURN_CODE -> Snackbar.LENGTH_LONG
            TASK_ARCHIVED_RETURN_CODE -> Snackbar.LENGTH_LONG
            else -> Snackbar.LENGTH_SHORT
        }
        val snackbar = Snackbar.make(layout, text, duration)
        when (returnCode) {
            TASK_DELETED_RETURN_CODE -> {
                snackbar.setAction("UNDO") {
                    val task: Task = data.getParcelableExtra(DELETED_TASK_CODE)
                        ?: throw Exception("Deleted task missing from returned intent")
                    val taskManager = TaskManager.getInstance(application)
                    taskManager.add(task)
                }
            }
            TASK_ARCHIVED_RETURN_CODE -> {
                snackbar.setAction("UNDO") {
                    val taskId = data.getIntExtra(ARCHIVED_TASK_CODE, -1)
                    if (taskId == -1) {
                        throw Exception("Archived task id missing from returned intent")
                    }
                    val taskManager = TaskManager.getInstance(application)
                    taskManager.unarchive(taskId)
                }
            }
        }
        snackbar.show()
    }
}
