package com.example.howie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val clickListener: (Task) -> Unit) :
    ListAdapter<Task, TaskAdapter.TaskViewHolder>(TasksComparator()) {

    class TaskViewHolder(view: View, private val clickListener: (Task) -> Unit) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val taskItem: TaskItem = view.findViewById(R.id.taskItem)

        init {
            view.setOnClickListener(this)
        }

        companion object {
            fun create(parent: ViewGroup, clickListener: (Task) -> Unit): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.task_row_item, parent, false)
                return TaskViewHolder(view, clickListener)
            }
        }

        override fun onClick(v: View?) {
            clickListener(taskItem.task)
        }
    }


    class TasksComparator : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder.create(parent, clickListener)

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.taskItem.task = getItem(position)
    }
}