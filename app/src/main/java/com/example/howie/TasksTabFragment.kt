package com.example.howie

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_tasks_object.*

class TasksTabFragment : Fragment(R.layout.fragment_tasks_tab) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager: ViewPager2 = view.findViewById(R.id.pager)
        viewPager.adapter = TasksTabAdapter(this)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
        val taskManager = TaskManager.getInstance(view.context)
        taskManager.currentTaskCounts.observe(this, Observer {
            setTab(0, it, tabLayout, "Do")
            setTab(1, it, tabLayout, "Decide")
            setTab(2, it, tabLayout, "Delegate")
            setTab(3, it, tabLayout, "Drop")
        })
        taskManager.lastInsertedTaskCategory.observe(this, Observer {
            tabLayout.getTabAt(it.ordinal)?.select()
        })
    }

    private fun setTab(
        position: Int,
        taskCountList: List<Int>,
        tabLayout: TabLayout,
        lowerText: String
    ) {
        val taskCount = taskCountList[position]
        val upperText = if (taskCount != 0) taskCount.toString() else "✓"
        tabLayout.getTabAt(position)!!.text = "$upperText\n$lowerText"
    }
}

class TasksTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        val tasksObjectFragment = TasksObjectFragment()
        val arguments = Bundle()
        arguments.putInt("position", position)
        tasksObjectFragment.arguments = arguments
        return tasksObjectFragment
    }
}

class TasksObjectFragment : Fragment(R.layout.fragment_tasks_object) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val taskManager = TaskManager.getInstance(activity!!.applicationContext)
        val position = arguments!!.getInt("position", 4)
        val unsnoozedTasks = when (position) {
            0 -> taskManager.doTasks
            1 -> taskManager.decideTasks
            2 -> taskManager.delegateTasks
            3 -> taskManager.dropTasks
            else -> taskManager.tasks
        }
        val snoozedTasks = when (position) {
            0 -> taskManager.snoozedDoTasks
            1 -> taskManager.snoozedDecideTasks
            2 -> taskManager.snoozedDelegateTasks
            3 -> taskManager.snoozedDropTasks
            else -> taskManager.tasks
        }
        setupView(unsnoozed_tasks_view, unsnoozedTasks, "Tasks", true)
        setupView(snoozed_tasks_view, snoozedTasks, "Snoozed Tasks", false)
    }

    private fun setupView(
        view: ExpandableTasksView,
        tasks: LiveData<List<Task>>,
        headerText: String,
        defaultExpandState: Boolean
    ) {
        view.setHeaderText(headerText)
        val taskAdapter = TaskAdapter {
            val intent = Intent(activity!!.applicationContext, TaskActivity::class.java)
            intent.putExtra("taskId", it)
            startActivity(intent)
        }
        view.setAdapter(taskAdapter)
        tasks.observe(this, Observer {
            if (it.isEmpty()) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
                view.setExpanded(defaultExpandState)
                it.let { taskAdapter.submitList(it) }
            }
        })
    }
}

