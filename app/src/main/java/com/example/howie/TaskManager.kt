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

    fun replace(oldTask: Task, newTask: Task) {
        // TODO: Implement
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }
}