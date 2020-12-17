package com.example.howie

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import java.util.*

class TaskManager(context: Context) : ViewModel() {

    private val database: TasksDatabase = TasksDatabaseSingleton.getDatabase(context)

    fun tasks(): LiveData<List<Task>> {
        return database.getTaskDao().getAll()
    }

    fun add(task: Task) {
        database.getTaskDao().insert(task)
    }

    fun rename(task: Task, name: String) {
        // TODO(Implement)
    }

    fun setImportance(task: Task, importance: Importance) {
        // TODO(Implement)
    }

    fun snooze(task: Task, snoozed: Calendar) {
        // TODO(Implement)
    }

    fun followUp(task: Task, due: Calendar) {
        // TODO(Implement)
    }

    fun setComplete(task: Task, completed: Calendar?) {
        // TODO(Implement)
    }

    fun remove(task: Task) {
        database.getTaskDao().delete(task)
    }
}