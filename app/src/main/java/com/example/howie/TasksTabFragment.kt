package com.example.howie

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
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
        viewPager.adapter = TasksTabAdapter(view.context, this)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
        val taskManager = TaskManager.getInstance(view.context)
        val observerFactory = {position: Int, lowerText: String ->
            Observer{tasks: List<Task> ->
                val upperText = if(tasks.isNotEmpty()) tasks.size.toString() else "✓"
                tabLayout.getTabAt(position)!!.text = "$upperText\n$lowerText"
            }
        }
        taskManager.doTasks.observe(this, observerFactory(0, "Do"))
        taskManager.decideTasks.observe(this, observerFactory(1, "Decide"))
        taskManager.delegateTasks.observe(this, observerFactory(2, "Delegate"))
        taskManager.dropTasks.observe(this, observerFactory(3, "Drop"))
    }
}

class TasksTabAdapter(private val context: Context, fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        val taskManager = TaskManager.getInstance(context)
        val tasks = when (position) {
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
        return TasksObjectFragment(tasks, snoozedTasks)
    }
}

class TasksObjectFragment(
    private val unsnoozedTasks: LiveData<List<Task>>,
    private val snoozedTasks: LiveData<List<Task>>
) : Fragment(R.layout.fragment_tasks_object) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupView(taskListView, unsnoozedTasks, view.context)
        setupView(snoozedTaskListView, snoozedTasks, view.context)
    }

    private fun setupView(view: RecyclerView, tasks: LiveData<List<Task>>, context: Context) {
        taskListView.layoutManager = LinearLayoutManager(context)
        val taskAdapter = TaskAdapter {
            val intent = Intent(activity!!.applicationContext, TaskActivity::class.java)
            intent.putExtra("taskId", it)
            startActivity(intent)
        }
        view.adapter = taskAdapter
        tasks.observe(this, Observer {
            it.let { taskAdapter.submitList(it) }
        })
    }
}

