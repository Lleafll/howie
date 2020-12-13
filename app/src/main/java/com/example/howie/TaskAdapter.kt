package com.example.howie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val task_manager: TaskManager) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.taskItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder  =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_row_item, parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = task_manager.tasks()[position].toString()
    }

    override fun getItemCount(): Int = task_manager.tasks().size
}