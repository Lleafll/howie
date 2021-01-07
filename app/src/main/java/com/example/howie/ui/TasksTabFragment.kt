package com.example.howie.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.howie.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TasksTabFragment : Fragment(R.layout.fragment_tasks_tab) {
    private val viewModel: TasksTabViewModel by viewModels {
        TasksTabViewModelFactory(requireActivity().application)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager: ViewPager2 = view.findViewById(R.id.pager)
        viewPager.adapter = TasksTabAdapter(this)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
        viewModel.counts.observe(viewLifecycleOwner, {
            setTab(0, it.doCount, tabLayout, "Do")
            setTab(1, it.decideCount, tabLayout, "Decide")
            setTab(2, it.delegateCount, tabLayout, "Delegate")
            setTab(3, it.dropCount, tabLayout, "Drop")
        })
        viewModel.lastInsertedTaskCategory.observe(viewLifecycleOwner, {
            tabLayout.getTabAt(it.ordinal)?.select()
        })
    }


    private fun setTab(position: Int, taskCount: Int, tabLayout: TabLayout, lowerText: String) {
        val upperText = if (taskCount != 0) taskCount.toString() else "✓"
        tabLayout.getTabAt(position)!!.text = "$upperText\n$lowerText"
    }
}