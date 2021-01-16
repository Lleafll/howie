package com.lorenz.howie.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.howie.R
import com.example.howie.databinding.FragmentTasksTabBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TasksTabFragment : Fragment() {
    private val _viewModel: MainViewModel by activityViewModels()
    private lateinit var _binding: FragmentTasksTabBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksTabBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                _binding.tabLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireActivity().applicationContext,
                        R.color.tabColorDark
                    )
                )
            }
        }
        val viewPager: ViewPager2 = view.findViewById(R.id.pager)
        viewPager.adapter = TasksTabAdapter(this)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
        _viewModel.tabLabels.observe(viewLifecycleOwner) { labels ->
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