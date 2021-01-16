package com.example.howie.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.howie.core.TaskIndex
import com.example.howie.databinding.TaskListHeaderBinding
import com.example.howie.databinding.TaskRowItemBinding

class TaskAdapter(private val title: String, private val adapterListener: Listener) :
    ListAdapter<TaskItemFields, TaskAdapter.ViewHolder>(TasksComparator()) {

    companion object {
        private const val VIEW_TYPE_ITEM = 1
        private const val VIEW_TYPE_HEADER = 2
    }

    interface Listener {
        fun onSnoozeToTomorrowClicked(index: TaskIndex)
        fun onRemoveSnoozeClicked(index: TaskIndex)
        fun onRescheduleClicked(index: TaskIndex)
        fun onArchiveClicked(index: TaskIndex)
        fun onUnarchiveClicked(index: TaskIndex)
        fun onEditClicked(index: TaskIndex)
    }

    private var selectedIndex: TaskIndex? = null

    sealed class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        class TaskViewHolder(binding: TaskRowItemBinding) : ViewHolder(binding.root) {
            val taskItem: TaskItem = binding.taskItem
        }

        class HeaderViewHolder(binding: TaskListHeaderBinding) : ViewHolder(binding.root) {
            val textView: TextView = binding.textView
        }
    }

    class TasksComparator : DiffUtil.ItemCallback<TaskItemFields>() {
        override fun areItemsTheSame(
            oldItem: TaskItemFields,
            newItem: TaskItemFields
        ): Boolean {
            return oldItem.index == newItem.index
        }

        override fun areContentsTheSame(
            oldItem: TaskItemFields,
            newItem: TaskItemFields
        ): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.due == newItem.due &&
                    oldItem.snoozed == newItem.snoozed
        }
    }

    override fun getItemCount(): Int = super.getItemCount() + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_ITEM -> ViewHolder.TaskViewHolder(
                TaskRowItemBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_HEADER -> ViewHolder.HeaderViewHolder(
                TaskListHeaderBinding.inflate(inflater, parent, false)
            )
            else -> error("Invalid viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder.TaskViewHolder -> {
                val taskItemFields = getItem(position - 1)
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
                        notifyDataSetChanged()
                    }
                })
            }
            is ViewHolder.HeaderViewHolder -> {
                holder.textView.text = title
            }
        }
    }
}
