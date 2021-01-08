package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.howie.core.TaskCategory
import com.example.howie.core.UnarchivedTasks
import com.example.howie.database.getDatabase
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class TasksObjectViewModel(application: Application) : AndroidViewModel(application) {
    private val _repository: TasksRepository

    init {
        val database = getDatabase(application.applicationContext)
        _repository = TasksRepository(database.getTaskDao(), database.getTaskListDao())
    }

    private var _taskList by Delegates.notNull<Int>()
    private var _category by Delegates.notNull<Int>()
    private var _tasks = MutableLiveData<UnarchivedTasks>()
    val tasks: LiveData<UnarchivedTasks> by this::_tasks

    fun initialize(taskList: Int, category: Int) = viewModelScope.launch {
        _taskList = taskList
        _category = category
        refreshTasks()
    }

    private suspend fun refreshTasks() {
        _tasks.value = _repository.getUnarchivedTasks(_taskList, TaskCategory.values()[_category])
    }

    fun snoozeToTomorrow(task: Int) {
        TODO("Implement")
    }
}