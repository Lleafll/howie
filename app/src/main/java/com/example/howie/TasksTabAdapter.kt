package com.example.howie

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

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