package com.example.howie

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        add_task_button.setOnClickListener {
            val intent = Intent(applicationContext, TaskActivity::class.java)
            startActivity(intent)
        }
        bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_show_archive -> {
                    val intent = Intent(applicationContext, ArchiveActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
        setupDrawer()
    }

    private fun setupDrawer() {
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            bottomAppBar,
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
            taskManager.switchToTaskList(itemId)
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
