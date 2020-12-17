package com.example.howie

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDate


class TaskManager(private val repository: TaskRepository) : ViewModel() {
    val tasks: LiveData<List<Task>> = repository.tasks

    fun add(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun rename(task: Task, name: String) {
        // TODO(Implement)
    }

    fun setImportance(task: Task, importance: Importance) {
        // TODO(Implement)
    }

    fun snooze(task: Task, snoozed: LocalDate) {
        // TODO(Implement)
    }

    fun followUp(task: Task, due: LocalDate) {
        // TODO(Implement)
    }

    fun setComplete(task: Task, completed: LocalDate?) {
        // TODO(Implement)
    }

    fun remove(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }
}