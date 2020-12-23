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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tasks_tab.*


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
        taskManager.taskLists.observe(this, Observer {
            nav_view.menu.removeGroup(R.id.list_groups)
            for (taskList in it) {
                val itemId = R.id.action_add_list + taskList.id + 1
                nav_view.menu.add(R.id.list_groups, itemId, Menu.NONE, taskList.name)
            }
            nav_view.setNavigationItemSelectedListener(this)
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val taskManager = TaskManager.getInstance(applicationContext)
        if (item.itemId == R.id.action_add_list) {
            taskManager.addTaskList("New Task List")
        } else {
            val itemId = item.itemId - R.id.action_add_list - 1
            taskManager.switchToTaskList(itemId.toLong())
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
