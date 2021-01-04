package com.example.howie

import android.content.Context
import android.graphics.drawable.Icon
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.expandable_task_list_view.view.*

class ExpandableTasksView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.expandable_task_list_view, this)
        taskListView.layoutManager = LinearLayoutManager(context)
        taskListView.isNestedScrollingEnabled = false
        header.setOnClickListener {
            when (taskListView.visibility) {
                View.VISIBLE -> this.setExpanded(false)
                else -> this.setExpanded(true)
            }
        }
    }

    fun setHeaderText(text: String) {
        header.text = text
    }

    fun setAdapter(taskAdapter: TaskAdapter) {
        taskListView.adapter = taskAdapter
    }

    fun setExpanded(shouldExpand: Boolean) {
        taskListView.visibility = if (shouldExpand) View.VISIBLE else View.GONE
        expandIndicator.setImageIcon(
            Icon.createWithResource(
                context,
                if (shouldExpand) R.drawable.outline_expand_less_24 else R.drawable.outline_expand_more_24
            )
        )
    }
}