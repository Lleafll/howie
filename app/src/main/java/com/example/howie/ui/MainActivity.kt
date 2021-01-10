package com.example.howie.ui

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
import com.example.howie.R
import com.example.howie.core.Task
import com.example.howie.core.TaskCategory
import com.example.howie.core.TaskIndex
import com.example.howie.core.TaskListIndex
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tasks_tab.*

const val SHOW_TASK_LIST_EXTRA = "showTaskList"
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
        setupTaskButton(add_task_button, viewModel)
        setupToolBar()
        setupDrawer(findViewById(R.id.drawer_layout), viewModel)
        setupColors()
    }

    private fun setupToolBar() {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_show_archive -> {
                    showArchive(viewModel.currentTaskList)
                    true
                }
                R.id.action_rename -> {
                    openRenameTaskListFragment(viewModel.currentTaskList)
                    true
                }
                R.id.action_delete -> {
                    openDeleteTaskListDialog(viewModel)
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
        viewModel.currentTaskListName.observe(this, {
            toolbar.title = it
        })
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val taskList: TaskListIndex? = intent.getParcelableExtra(SHOW_TASK_LIST_EXTRA)
        if (taskList != null) {
            viewModel.setTaskList(taskList)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add_list) {
            viewModel.addTaskList()
        } else {
            val taskList = TaskListIndex(item.itemId - R.id.action_add_list - 1)
            viewModel.setTaskList(taskList)
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
                    throw Exception("Supply ${::TASK_RETURN_CODE.name} data when exiting $TaskActivity")
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
    mainViewModel.taskListDrawerLabels.observe(this, { taskListNamesAndCounts ->
        buildDrawerContent(taskListNamesAndCounts, nav_view.menu)
    })
    nav_view.setNavigationItemSelectedListener(this)
}

private fun buildDrawerContent(labels: List<String>, menu: Menu) {
    menu.removeGroup(R.id.list_groups)
    labels.forEachIndexed { index, label ->
        val itemId = buildNavigationItemId(index)
        menu.add(R.id.list_groups, itemId, Menu.NONE, label)
    }
}

private fun MainActivity.setupTaskButton(button: FloatingActionButton, viewModel: MainViewModel) {
    button.setOnClickListener {
        val intent = Intent(applicationContext, TaskActivity::class.java)
        intent.putExtra(
            TaskActivity.TASK_CATEGORY,
            TaskCategory.values()[tab_layout.selectedTabPosition]
        )
        intent.putExtra(TaskActivity.TASK_LIST_INDEX, viewModel.currentTaskList)
        startActivity(intent)
    }
}

private fun buildNavigationItemId(taskListIndex: Int) =
    R.id.action_add_list + taskListIndex + 1


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
                mainViewModel.addTask(task)
            }
        }
        TASK_ARCHIVED_RETURN_CODE -> {
            snackbar.setAction("UNDO") {
                val taskId = data.getParcelableExtra<TaskIndex>(ARCHIVED_TASK_CODE)!!
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

private fun MainActivity.openRenameTaskListFragment(taskList: TaskListIndex) {
    val dialog = RenameTaskListFragment()
    val arguments = Bundle()
    arguments.putParcelable(TASK_LIST_ID_ARGUMENT, taskList)
    dialog.arguments = arguments
    dialog.show(supportFragmentManager, "renameTask")
}

private fun MainActivity.openDeleteTaskListDialog(mainViewModel: MainViewModel) {
    val builder = AlertDialog.Builder(this)
    builder.setMessage("Delete Task List?")
        .setPositiveButton("Yes") { _, _ ->
            mainViewModel.deleteTaskList(mainViewModel.currentTaskList)
        }
        .setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
    val alert = builder.create()
    alert.show()
}

private fun MainActivity.showArchive(currentTaskList: TaskListIndex) {
    val intent = Intent(applicationContext, ArchiveActivity::class.java)
    intent.putExtra(ArchiveActivity.TASKLIST_INDEX, currentTaskList)
    startActivity(intent)
}