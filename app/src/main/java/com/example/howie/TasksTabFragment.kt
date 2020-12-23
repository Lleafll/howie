package com.example.howie

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        val observerFactory = { position: Int, lowerText: String ->
            Observer { tasks: List<Task> ->
                val upperText = if (tasks.isNotEmpty()) tasks.size.toString() else "✓"
                tabLayout.getTabAt(position)!!.text = "$upperText\n$lowerText"
            }
        }
        taskManager.doTasks.observe(this, observerFactory(0, "Do"))
        taskManager.decideTasks.observe(this, observerFactory(1, "Decide"))
        taskManager.delegateTasks.observe(this, observerFactory(2, "Delegate"))
        taskManager.dropTasks.observe(this, observerFactory(3, "Drop"))
        taskManager.lastInsertedTaskCategory.observe(this, Observer {
            tabLayout.getTabAt(it.ordinal)?.select()
        })
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
        setupView(taskListView, unsnoozedTasks, view.context, tasksTextView)
        setupView(snoozedTaskListView, snoozedTasks, view.context, snoozedTasksTextView)
    }

    private fun setupView(
        view: RecyclerView,
        tasks: LiveData<List<Task>>,
        context: Context,
        header: TextView
    ) {
        taskListView.layoutManager = LinearLayoutManager(context)
        val taskAdapter = TaskAdapter {
            val intent = Intent(activity!!.applicationContext, TaskActivity::class.java)
            intent.putExtra("taskId", it)
            startActivity(intent)
        }
        view.adapter = taskAdapter
        tasks.observe(this, Observer {
            header.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
            it.let { taskAdapter.submitList(it) }
        })
        header.setOnClickListener {
            view.visibility = when (view.visibility) {
                View.GONE -> View.VISIBLE
                View.VISIBLE -> View.GONE
                View.INVISIBLE -> View.VISIBLE
                else -> View.VISIBLE
            }
        }
    }
}

