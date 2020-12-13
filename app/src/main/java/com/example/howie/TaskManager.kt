package com.example.howie

import java.util.*

class TaskManager(private val tasks: MutableList<Task> = mutableListOf()) {

    fun tasks(): List<Task> {
        return tasks
    }

    private fun replace(old_task: Task, new_task: Task) {
        val index = tasks.indexOf(old_task)
        tasks[index] = new_task
    }

    fun rename(task: Task, name: String) {
        val newTask = task.copy(name = name)
        replace(task, newTask)
    }

    fun setImportance(task: Task, importance: Importance) {
        val newTask = task.copy(importance = importance)
        replace(task, newTask)
    }

    fun snooze(task: Task, snoozed: Date) {
        val newTask = task.copy(snoozed = snoozed)
        replace(task, newTask)
    }

    fun followUp(task: Task, due: Date) {
        val newTask = task.copy(due = due)
        replace(task, newTask)
    }

    fun setComplete(task: Task, completed: Date?) {
        val newTask = task.copy(completed = completed)
        replace(task, newTask)
    }
}