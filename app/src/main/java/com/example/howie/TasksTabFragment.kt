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

class TasksTabFragment : Fragment(R.layout.fragment_tasks_tab) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager: ViewPager2 = view.findViewById(R.id.pager)
        viewPager.adapter = TasksTabAdapter(view.context,this)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Do"
                1 -> "Decide"
                2 -> "Delegate"
                3 -> "Drop"
                else -> position.toString()
            }
        }.attach()
    }
}

class TasksTabAdapter(private val context: Context, fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        val taskManager = TaskManager.getInstance(context)
        val tasks = when(position) {
            0 -> taskManager.doTasks
            1 -> taskManager.decideTasks
            2 -> taskManager.delegateTasks
            3 -> taskManager.dropTasks
            else -> taskManager.tasks
        }
        return TasksObjectFragment(tasks)
    }
}

class TasksObjectFragment(private val filteredTasks: LiveData<List<Task>>) :
    Fragment(R.layout.fragment_tasks_object) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView: RecyclerView = view.findViewById(R.id.taskListView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val taskAdapter = TaskAdapter {
            val intent = Intent(activity!!.applicationContext, TaskActivity::class.java)
            intent.putExtra("taskId", it)
            startActivity(intent)
        }
        recyclerView.adapter = taskAdapter
        filteredTasks.observe(this, Observer { tasks ->
            tasks.let { taskAdapter.submitList(it) }
        })
    }
}
