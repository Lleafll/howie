package com.example.howie.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.howie.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TasksTabFragment : Fragment(R.layout.fragment_tasks_tab) {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager: ViewPager2 = view.findViewById(R.id.pager)
        viewPager.adapter = TasksTabAdapter(this)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
        viewModel.tabLabels.observe(viewLifecycleOwner) { labels ->
            setTab(0, tabLayout, labels.label0)
            setTab(1, tabLayout, labels.label1)
            setTab(2, tabLayout, labels.label2)
            setTab(3, tabLayout, labels.label3)
        }
    }
}

private fun setTab(position: Int, tabLayout: TabLayout, text: String) {
    tabLayout.getTabAt(position)!!.text = text
}