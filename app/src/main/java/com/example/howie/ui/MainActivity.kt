package com.example.howie.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
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
import com.example.howie.databinding.ActivityMainBinding
import com.example.howie.databinding.SidebarListItemBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import java.time.LocalDate

const val SHOW_TASK_LIST_EXTRA = "showTaskList"
const val TASK_ACTIVITY_REQUEST_CODE = 1
private const val ARCHIVE_ACTIVITY_REQUEST_CODE = 2
const val TASK_RETURN_CODE = "TaskReturnCode"
const val DELETED_TASK_CODE = "DeletedTask"
const val ARCHIVED_TASK_CODE = "ArchivedTask"
const val TASK_DELETED_RETURN_CODE = 0
const val TASK_ARCHIVED_RETURN_CODE = 1
const val TASK_MOVED_RETURN_CODE = 2

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(application) }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupTaskButton(binding.addTaskButton, viewModel)
        setupToolBar()
        setupDrawer(binding.drawerLayout, viewModel, binding)
        setupColors()
        setupSnackbar(viewModel)
    }

    private fun setupToolBar() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_show_archive -> {
                    showArchive(viewModel.currentTaskList)
                    true
                }
                R.id.action_rename -> {
                    openRenameTaskListFragment(viewModel.currentTaskList, viewModel)
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
            binding.toolbar.title = it
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
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.forceRefresh()
        if (requestCode == TASK_ACTIVITY_REQUEST_CODE) {
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

private fun MainActivity.setupSnackbar(viewModel: MainViewModel) {
    val layout: CoordinatorLayout = findViewById(R.id.main_coordinator_layout)
    viewModel.taskArchivedNotificationEvent.observe(
        this,
        showArchivedNotification(layout, viewModel)
    )
    viewModel.taskDeletedNotificationEvent.observe(this, showDeletedNotification(layout, viewModel))
    viewModel.taskSnoozedToTomorrowNotificationEvent.observe(
        this,
        showSnoozedToTomorrowNotification(layout, viewModel)
    )
    viewModel.snoozeRemovedNotificationEvent.observe(
        this,
        showSnoozedRemovedNotification(layout, viewModel)
    )
    viewModel.taskScheduledNotificationEvent.observe(this, showScheduleNotification(layout))
}

private fun showScheduleNotification(
    layout: CoordinatorLayout
) = { _: Boolean ->
    val snackbar = Snackbar.make(layout, "Task rescheduled", Snackbar.LENGTH_SHORT)
    snackbar.show()
}

private fun showSnoozedRemovedNotification(
    layout: CoordinatorLayout,
    viewModel: MainViewModel
) = { it: Pair<TaskIndex, LocalDate> ->
    val (task: TaskIndex, snooze: LocalDate) = it
    val snackbar = Snackbar.make(layout, "Task unsnoozed", Snackbar.LENGTH_LONG)
    snackbar.setAction("UNDO") { viewModel.addSnooze(task, snooze) }
    snackbar.show()
}

private fun showSnoozedToTomorrowNotification(
    layout: CoordinatorLayout, viewModel: MainViewModel
) = { task: TaskIndex ->
    val snackbar = Snackbar.make(layout, "Task snoozed to tomorrow", Snackbar.LENGTH_LONG)
    snackbar.setAction("UNDO") { viewModel.removeSnooze(task) }
    snackbar.show()
}

private fun showDeletedNotification(
    layout: CoordinatorLayout, viewModel: MainViewModel
) = { task: Task ->
    val snackbar = Snackbar.make(layout, "Task deleted", Snackbar.LENGTH_LONG)
    snackbar.setAction("UNDO") { viewModel.addTask(task) }
    snackbar.show()
}

private fun showArchivedNotification(
    layout: CoordinatorLayout, viewModel: MainViewModel
) = { taskIndex: TaskIndex ->
    val snackbar = Snackbar.make(layout, "Task archived", Snackbar.LENGTH_LONG)
    snackbar.setAction("UNDO") { viewModel.unarchive(taskIndex) }
    snackbar.show()
}

private fun MainActivity.setupDrawer(
    drawer: DrawerLayout,
    mainViewModel: MainViewModel,
    binding: ActivityMainBinding
) {
    val toggle = ActionBarDrawerToggle(
        this,
        drawer,
        binding.toolbar,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close
    )
    drawer.addDrawerListener(toggle)
    toggle.syncState()
    mainViewModel.taskListDrawerContent.observe(this, { taskListNamesAndCounts ->
        buildDrawerHeaderContent(
            taskListNamesAndCounts,
            binding,
            mainViewModel
        )
    })
    binding.navView.getHeaderView(0).findViewById<Button>(R.id.add_new_list_button)
        .setOnClickListener {
            closeDrawer(binding)
            mainViewModel.addTaskList()
        }
    binding.navView.setNavigationItemSelectedListener(this)
}

private fun MainActivity.buildDrawerHeaderContent(
    content: TaskListDrawerContent,
    mainBinding: ActivityMainBinding,
    viewModel: MainViewModel
) {
    val headerLayout = mainBinding.navView.getHeaderView(0) as LinearLayout
    val taskListLayout: LinearLayout = headerLayout.findViewById(R.id.task_lists)
    taskListLayout.removeAllViews()
    content.labels.forEachIndexed { index, label ->
        val binding = SidebarListItemBinding.inflate(
            LayoutInflater.from(applicationContext),
            taskListLayout,
            true
        )
        binding.root.apply {
            text = label
            textAlignment = Button.TEXT_ALIGNMENT_VIEW_START
            if (index == content.selectedIndex) {
                setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
            setOnClickListener {
                closeDrawer(mainBinding)
                viewModel.setTaskList(TaskListIndex(index))
            }
        }
    }
}

private fun closeDrawer(binding: ActivityMainBinding) {
    binding.drawerLayout.closeDrawer(GravityCompat.START)
}

private fun MainActivity.setupTaskButton(button: FloatingActionButton, viewModel: MainViewModel) {
    button.setOnClickListener {
        val intent = Intent(applicationContext, TaskActivity::class.java)
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        intent.putExtra(
            TaskActivity.TASK_CATEGORY,
            TaskCategory.values()[tabLayout.selectedTabPosition]
        )
        intent.putExtra(TaskActivity.TASK_LIST_INDEX, viewModel.currentTaskList)
        startActivityForResult(intent, TASK_ACTIVITY_REQUEST_CODE)
    }
}

private fun handleTaskActivityReturn(
    returnCode: Int, data: Intent, layout: CoordinatorLayout, mainViewModel: MainViewModel
) {
    when (returnCode) {
        TASK_DELETED_RETURN_CODE -> {
            val task: Task = data.getParcelableExtra(DELETED_TASK_CODE)
                ?: error("Deleted task missing from returned intent")
            mainViewModel.taskDeletedNotificationEvent.value = task
        }
        TASK_ARCHIVED_RETURN_CODE -> {
            val taskId = data.getParcelableExtra<TaskIndex>(ARCHIVED_TASK_CODE)!!
            mainViewModel.taskArchivedNotificationEvent.value = taskId
        }
        TASK_MOVED_RETURN_CODE -> {
            val snackbar = Snackbar.make(layout, "Task Moved", Snackbar.LENGTH_SHORT)
            snackbar.show()
        }
    }
}

private fun MainActivity.setupColors() {
    setupActivityColors(resources, window, applicationContext)
}

private fun MainActivity.openRenameTaskListFragment(
    taskList: TaskListIndex,
    viewModel: MainViewModel
) {
    val dialog = RenameTaskListFragment()
    val arguments = Bundle()
    arguments.putParcelable(TASK_LIST_ID_ARGUMENT, taskList)
    dialog.arguments = arguments
    dialog.onOkClicked = { viewModel.renameTaskList(it) }
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
    startActivityForResult(intent, ARCHIVE_ACTIVITY_REQUEST_CODE)
}