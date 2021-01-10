package com.example.howie.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.howie.R
import com.example.howie.core.IndexedTask

class TaskAdapter(private val adapterListener: Listener) :
    ListAdapter<IndexedTask, TaskAdapter.TaskViewHolder>(TasksComparator()) {

    interface Listener {
        fun onSnoozeToTomorrowClicked(index: Int)
        fun onRemoveSnoozeClicked(index: Int)
        fun onRescheduleClicked(index: Int)
        fun onArchiveClicked(index: Int)
        fun onUnarchiveClicked(index: Int)
        fun onEditClicked(index: Int)
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

    class TasksComparator : DiffUtil.ItemCallback<IndexedTask>() {
        override fun areItemsTheSame(oldItem: IndexedTask, newItem: IndexedTask): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: IndexedTask, newItem: IndexedTask): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder.create(parent)

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val indexedTask = getItem(position)
        holder.taskItem.task = indexedTask.task
        val index = indexedTask.indexInTaskList
        holder.taskItem.setListener(object : TaskItem.Listener {
            override fun onSnoozeToTomorrowClicked() =
                adapterListener.onSnoozeToTomorrowClicked(position)

            override fun onRemoveSnoozeClicked() = adapterListener.onRemoveSnoozeClicked(index)
            override fun onRescheduleClicked() = adapterListener.onRescheduleClicked(index)
            override fun onArchiveClicked() = adapterListener.onArchiveClicked(index)
            override fun onUnarchiveClicked() = adapterListener.onUnarchiveClicked(index)
            override fun onEditClicked() = adapterListener.onEditClicked(index)
        })
    }
}