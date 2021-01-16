package com.lorenz.howie.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lorenz.howie.databinding.TaskListHeaderBinding
import com.lorenz.howie.databinding.TaskRowItemBinding
import com.lorenz.howie.core.TaskIndex
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class TaskAdapter(
    private val fields: List<TaskItemFields>,
    private val title: String,
    private val adapterListener: Listener
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_ITEM = 1
        private const val VIEW_TYPE_HEADER = 2

        private const val IC_EXPANDED_ROTATION_DEG = 0F
        private const val IC_COLLAPSED_ROTATION_DEG = 180F
    }

    interface Listener {
        fun onSnoozeToTomorrowClicked(index: TaskIndex)
        fun onRemoveSnoozeClicked(index: TaskIndex)
        fun onRescheduleClicked(index: TaskIndex)
        fun onArchiveClicked(index: TaskIndex)
        fun onUnarchiveClicked(index: TaskIndex)
        fun onEditClicked(index: TaskIndex)
    }

    sealed class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        class TaskViewHolder(binding: TaskRowItemBinding) : ViewHolder(binding.root) {
            val taskItem: TaskItem = binding.taskItem
        }

        class HeaderViewHolder(binding: TaskListHeaderBinding) : ViewHolder(binding.root) {
            val textView: TextView = binding.textView
            val expandIndicator: ImageView = binding.icExpand
        }
    }

    private var selectedIndex: TaskIndex? = null

    private var isExpanded: Boolean by Delegates.observable(true) { _: KProperty<*>, _: Boolean, newExpandedValue: Boolean ->
        if (newExpandedValue) {
            notifyItemRangeInserted(1, fields.size)
            notifyItemChanged(0)
        } else {
            notifyItemRangeRemoved(1, fields.size)
            notifyItemChanged(0)
        }
    }

    private val onHeaderClickListener = View.OnClickListener {
        isExpanded = !isExpanded
    }

    override fun getItemCount(): Int = if (isExpanded) fields.size + 1 else 1

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
                val taskItemFields = fields[position - 1]
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
                holder.expandIndicator.rotation =
                    if (isExpanded) IC_EXPANDED_ROTATION_DEG else IC_COLLAPSED_ROTATION_DEG
                holder.expandIndicator.setOnClickListener(onHeaderClickListener)
            }
        }
    }
}
