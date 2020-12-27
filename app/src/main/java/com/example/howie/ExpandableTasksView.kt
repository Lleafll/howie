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
        header.setOnClickListener {
            taskListView.visibility = when (taskListView.visibility) {
                View.GONE -> View.VISIBLE
                View.VISIBLE -> View.GONE
                View.INVISIBLE -> View.VISIBLE
                else -> View.VISIBLE
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
        expandIndicator.setImageIcon(if(shouldExpand) Icon.createWithResource(context, drawable))
    }
}