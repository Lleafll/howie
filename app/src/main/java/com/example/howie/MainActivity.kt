package com.example.howie

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.size
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tasks_tab.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        add_task_button.setOnClickListener {
            val intent = Intent(applicationContext, TaskActivity::class.java)
            startActivity(intent)
        }
        setupToolBar()
        setupDrawer()
        setupColors()
    }

    private fun setupColors() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                tab_layout.setBackgroundColor(
                    ContextCompat.getColor(applicationContext, R.color.tabColorDark)
                )
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
        val taskManager = TaskManager.getInstance(applicationContext)
        lifecycleScope.launch {
            taskManager.currentTaskList.collect { toolbar.title = it.name }
        }
    }

    private fun onRenameClick() {
        val dialog = RenameTaskListFragment()
        dialog.show(supportFragmentManager, "renameTask")
    }

    private fun onDeleteClick() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setMessage("Delete Task List?")
            .setPositiveButton("Yes") { _, _ ->
                TaskManager.getInstance(applicationContext).deleteCurrentTaskList()
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
        val taskManager = TaskManager.getInstance(applicationContext)
        // TODO: Causes really ugly race conditions
        lifecycleScope.launch {
            taskManager.taskLists.collect { buildDrawerContent() }
            taskManager.tasks.collect { updateDrawerContent() }
        }
    }

    private fun buildDrawerContent() {
        val taskManager = TaskManager.getInstance(applicationContext)
        val listener = this
        lifecycleScope.launch {
            val taskLists = taskManager.taskLists.first()
            nav_view.menu.removeGroup(R.id.list_groups)
            for (taskList in taskLists) {
                taskManager.getTaskCounts(taskList.id).collect { taskCounts ->
                    val itemId = R.id.action_add_list + taskList.id.toInt() + 1
                    val name = buildDrawerItemName(taskList, taskCounts)
                    val item = nav_view.menu.add(R.id.list_groups, itemId, Menu.NONE, name)
                    if (taskManager.currentTaskListId == taskList.id) {
                        item.isChecked = true
                    }
                }
            }
            nav_view.setNavigationItemSelectedListener(listener)
        }
    }

    private fun updateDrawerContent() {
        val taskManager = TaskManager.getInstance(applicationContext)
        val listener = this
        lifecycleScope.launch {
            val taskLists = taskManager.taskLists.first()
            for (taskList in taskLists) {
                taskManager.getTaskCounts(taskList.id).collect { taskCounts ->
                    val itemId = R.id.action_add_list + taskList.id.toInt() + 1
                    val item = nav_view.menu.findItem(itemId)
                    if (item != null) {
                        item.title = buildDrawerItemName(taskList, taskCounts)
                    }
                }
            }
            nav_view.setNavigationItemSelectedListener(listener)
        }

    }

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
        val taskManager = TaskManager.getInstance(applicationContext)
        if (item.itemId == R.id.action_add_list) {
            taskManager.addTaskList("New Task List")
        } else {
            for (i in 0 until nav_view.menu.size) {
                nav_view.menu.getItem(i).isChecked = false
            }
            item.isChecked = true
            val itemId = item.itemId - R.id.action_add_list - 1
            taskManager.switchToTaskList(itemId.toLong())
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
