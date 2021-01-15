package com.example.howie.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.howie.R
import com.example.howie.core.TaskIndex

class TaskAdapter(private val adapterListener: Listener) :
    ListAdapter<TaskItemFields, TaskAdapter.TaskViewHolder>(TasksComparator()) {

    private var selectedIndex: TaskIndex? = null
    private var selectedPosition: Int = -1

    interface Listener {
        fun onSnoozeToTomorrowClicked(index: TaskIndex)
        fun onRemoveSnoozeClicked(index: TaskIndex)
        fun onRescheduleClicked(index: TaskIndex)
        fun onArchiveClicked(index: TaskIndex)
        fun onUnarchiveClicked(index: TaskIndex)
        fun onEditClicked(index: TaskIndex)
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

    class TasksComparator : DiffUtil.ItemCallback<TaskItemFields>() {
        override fun areItemsTheSame(oldItem: TaskItemFields, newItem: TaskItemFields): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: TaskItemFields, newItem: TaskItemFields): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder.create(parent)

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val taskItemFields = getItem(position)
        val taskItem = holder.taskItem
        taskItem.setFields(taskItemFields)
        val index = taskItemFields.index
        if (index == selectedIndex) {
            taskItem.expand()
        } else {
            taskItem.collapse()
        }
        taskItem.setListener(object : TaskItem.Listener {
            override fun onSnoozeToTomorrowClicked() {
                adapterListener.onSnoozeToTomorrowClicked(index)
            }

            override fun onRemoveSnoozeClicked() {
                adapterListener.onRemoveSnoozeClicked(index)
            }

            override fun onRescheduleClicked() {
                adapterListener.onRescheduleClicked(index)
            }

            override fun onArchiveClicked() {
                adapterListener.onArchiveClicked(index)
            }

            override fun onUnarchiveClicked() {
                adapterListener.onUnarchiveClicked(index)
            }

            override fun onEditClicked() {
                adapterListener.onEditClicked(index)
            }

            override fun onSelected() {
                selectedIndex = if (taskItem.isExpanded()) null else index
                val previousSelectedPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(selectedPosition)
            }
        })
    }
}