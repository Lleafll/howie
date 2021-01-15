package com.example.howie.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.howie.core.TaskIndex
import com.example.howie.databinding.TaskRowItemBinding

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

    class TaskViewHolder(binding: TaskRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val taskItem: TaskItem = binding.taskItem
    }

    class TasksComparator : DiffUtil.ItemCallback<TaskItemFields>() {
        override fun areItemsTheSame(oldItem: TaskItemFields, newItem: TaskItemFields): Boolean {
            return oldItem.index == newItem.index
        }

        override fun areContentsTheSame(oldItem: TaskItemFields, newItem: TaskItemFields): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.due == newItem.due &&
                    oldItem.snoozed == newItem.snoozed
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            TaskRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

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