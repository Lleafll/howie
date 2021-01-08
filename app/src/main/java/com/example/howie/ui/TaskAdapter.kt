package com.example.howie.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.howie.R
import com.example.howie.core.Task

class TaskAdapter(private val adapterListener: Listener) :
    ListAdapter<Task, TaskAdapter.TaskViewHolder>(TasksComparator()) {

    interface Listener {
        fun onSnoozeToTomorrowClicked(position: Int)
        fun onRemoveSnoozeClicked(position: Int)
        fun onRescheduleClicked(position: Int)
        fun onArchiveClicked(position: Int)
        fun onUnarchiveClicked(position: Int)
        fun onEditClicked(position: Int)
    }

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskItem: TaskItem = view.findViewById(R.id.taskItem)

        companion object {
            fun create(parent: ViewGroup): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.task_row_item, parent, false)
                return TaskViewHolder(view)
            }
        }
    }

    class TasksComparator : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem === newItem
        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder.create(parent)

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.taskItem.task = getItem(position)
        holder.taskItem.setListener(object : TaskItem.Listener {
            override fun onSnoozeToTomorrowClicked() =
                adapterListener.onSnoozeToTomorrowClicked(position)

            override fun onRemoveSnoozeClicked() = adapterListener.onRemoveSnoozeClicked(position)
            override fun onRescheduleClicked() = adapterListener.onRescheduleClicked(position)
            override fun onArchiveClicked() = adapterListener.onArchiveClicked(position)
            override fun onUnarchiveClicked() = adapterListener.onUnarchiveClicked(position)
            override fun onEditClicked() = adapterListener.onEditClicked(position)
        })
    }
}