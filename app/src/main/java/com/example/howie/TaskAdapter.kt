package com.example.howie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val task_manager: TaskManager, private val clickListener: (Task)-> Unit) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    class ViewHolder(view: View, private val clickListener: (Task)-> Unit) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val taskItem: TaskItem  = view.findViewById(R.id.taskItem)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener(taskItem.task)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder  =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_row_item, parent, false), clickListener)


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.taskItem.task = task_manager.tasks()[position]
    }

    override fun getItemCount(): Int = task_manager.tasks().size

}