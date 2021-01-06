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
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
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
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupTaskButton(add_task_button, tab_layout)
        setupToolBar()
        setupDrawer(findViewById(R.id.drawer_layout), viewModel)
        setupColors()
        broadcastDatabaseChanges(viewModel)  // This is hacky but the best way to update the widgets
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
                    onRenameClick(viewModel)
                    true
                }
                R.id.action_delete -> {
                    onDeleteClick(viewModel)
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
        viewModel.currentTaskList.observe(this, {
            toolbar.title = it.name
        })
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val taskListId = intent.getLongExtra(SHOW_TASK_LIST_EXTRA, -1L)
        if (taskListId != -1L) {
            viewModel.switchToTaskList(taskListId)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add_list) {
            viewModel.addTaskList("New Task List")
        } else {
            val taskListId = item.itemId - R.id.action_add_list - 1
            viewModel.switchToTaskList(taskListId.toLong())
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
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
                handleTaskActivityReturn(
                    returnCode,
                    data,
                    findViewById(R.id.main_coordinator_layout),
                    viewModel
                )
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

private fun MainActivity.setupDrawer(drawer: DrawerLayout, mainViewModel: MainViewModel) {
    val toggle = ActionBarDrawerToggle(
        this,
        drawer,
        toolbar,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close
    )
    drawer.addDrawerListener(toggle)
    toggle.syncState()
    mainViewModel.getTaskListNamesAndCounts().observe(this, { taskListNamesAndCounts ->
        buildDrawerContent(taskListNamesAndCounts, nav_view.menu)
    })
    nav_view.setNavigationItemSelectedListener(this)
}

private fun buildDrawerContent(
    taskListNamesAndCounts: List<TaskListNameAndCount>,
    menu: Menu
) {
    menu.removeGroup(R.id.list_groups)
    taskListNamesAndCounts.forEach { taskListNameAndCount ->
        val itemId = buildNavigationItemId(taskListNameAndCount.id)
        val name = buildDrawerItemName(taskListNameAndCount.name, taskListNameAndCount.count)
        menu.add(R.id.list_groups, itemId, Menu.NONE, name)
    }
}

private fun MainActivity.broadcastDatabaseChanges(mainViewModel: MainViewModel) {
    mainViewModel.tasks.observe(this, {
        val intent = Intent(DATABASE_UPDATE, null, this, HowieAppWidgetProvider::class.java)
        sendBroadcast(intent)
    })
}

private fun MainActivity.setupTaskButton(button: FloatingActionButton, tabLayout: TabLayout) {
    button.setOnClickListener {
        val intent = Intent(applicationContext, TaskActivity::class.java)
        intent.putExtra(TASK_CATEGORY, tabLayout.selectedTabPosition)
        startActivity(intent)
    }
}

private fun buildNavigationItemId(taskListId: Long) =
    R.id.action_add_list + taskListId.toInt() + 1

private fun buildDrawerItemName(name: String, taskCounts: TaskCounts): String {
    return "$name (" +
            "${countToString(taskCounts.doCount)}/" +
            "${countToString(taskCounts.decideCount)}/" +
            "${countToString(taskCounts.delegateCount)}/" +
            "${countToString(taskCounts.dropCount)})"
}

private fun countToString(count: Int) = when (count) {
    0 -> "✓"
    else -> count.toString()
}

private fun handleTaskActivityReturn(
    returnCode: Int, data: Intent, layout: CoordinatorLayout, mainViewModel: MainViewModel
) {
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
                mainViewModel.add(task)
            }
        }
        TASK_ARCHIVED_RETURN_CODE -> {
            snackbar.setAction("UNDO") {
                val taskId = data.getIntExtra(ARCHIVED_TASK_CODE, -1)
                if (taskId == -1) {
                    throw Exception("Archived task id missing from returned intent")
                }
                mainViewModel.unarchive(taskId)
            }
        }
    }
    snackbar.show()
}

private fun MainActivity.setupColors() {
    setupActivityColors(resources, window, applicationContext)
    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> {
            tab_layout.setBackgroundColor(
                ContextCompat.getColor(applicationContext, R.color.tabColorDark)
            )
        }
    }
}

private fun MainActivity.onRenameClick(mainViewModel: MainViewModel) {
    mainViewModel.currentTaskListId.observeOnce(this, { taskListId ->
        val dialog = RenameTaskListFragment()
        val arguments = Bundle()
        arguments.putLong(TASK_LIST_ID_ARGUMENT, taskListId)
        dialog.arguments = arguments
        dialog.show(supportFragmentManager, "renameTask")
    })
}

private fun MainActivity.onDeleteClick(mainViewModel: MainViewModel) {
    mainViewModel.currentTaskListId.observeOnce(this, { taskListId ->
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Delete Task List?")
            .setPositiveButton("Yes") { _, _ ->
                mainViewModel.deleteTaskList(taskListId)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    })
}