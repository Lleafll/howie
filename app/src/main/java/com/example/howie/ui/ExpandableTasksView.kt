package com.example.howie.ui

import android.content.Context
import android.graphics.drawable.Icon
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.howie.R
import com.example.howie.databinding.ExpandableTaskListViewBinding

class ExpandableTasksView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private val binding =
        ExpandableTaskListViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.taskListView.layoutManager = LinearLayoutManager(context)
        binding.taskListView.isNestedScrollingEnabled = false
        binding.header.setOnClickListener {
            when (binding.taskListView.visibility) {
                View.VISIBLE -> this.setExpanded(false)
                else -> this.setExpanded(true)
            }
        }
    }

    fun setHeaderText(text: String) {
        binding.header.text = text
    }

    fun setAdapter(taskAdapter: TaskAdapter) {
        binding.taskListView.adapter = taskAdapter
    }

    fun setExpanded(shouldExpand: Boolean) {
        binding.taskListView.visibility = if (shouldExpand) View.VISIBLE else View.GONE
        binding.expandIndicator.setImageIcon(
            Icon.createWithResource(
                context,
                if (shouldExpand) R.drawable.outline_expand_less_24 else R.drawable.outline_expand_more_24
            )
        )
    }
}