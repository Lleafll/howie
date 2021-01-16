package com.lorenz.howie.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lorenz.howie.core.TaskCategory

class TasksTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        val fragment = TasksObjectFragment()
        val arguments = Bundle()
        arguments.putSerializable(
            TasksObjectFragment.TASK_CATEGORY_ARGUMENT,
            TaskCategory.values()[position]
        )
        fragment.arguments = arguments
        return fragment
    }
}