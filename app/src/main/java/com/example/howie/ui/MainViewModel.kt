package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.howie.core.Task
import com.example.howie.core.TaskCounts
import com.example.howie.database.getDatabase
import kotlinx.coroutines.launch

const val HOWIE_SHARED_PREFERENCES_KEY = "howie_default_shared_preferences"

data class TaskListNameAndCount(
    val id: Long,
    val name: String,
    val count: TaskCounts
)

class MainViewModel(application: Application, private val repository: TasksRepository) :
    AndroidViewModel(application) {

    constructor(application: Application) : this(application, {
        val database = getDatabase(application.applicationContext)
        TasksRepository(database.getTaskDao(), database.getTaskListDao())
    }())

    var currentTaskList = 0
        set(value) {
            field = value
            // TODO: Implement
        }
    private val _currentTaskListName = MutableLiveData<String>()
    val currentTaskListName: LiveData<String> by this::_currentTaskListName

    fun addTask(task: Task) = viewModelScope.launch {
        // TODO: Implement
    }

    fun doArchive(id: Int) = viewModelScope.launch {
        // TODO: Implement
    }

    fun unarchive(id: Int) = viewModelScope.launch {
        // TODO: Implement
    }

    fun delete(id: Int) = viewModelScope.launch {
        // TODO: Implement
    }

    fun addTaskList(name: String) = viewModelScope.launch {
        // TODO: Implement
    }

    fun deleteTaskList(taskList: Int) = viewModelScope.launch {
        // TODO: Implement
    }

    fun getTaskListNamesAndCounts(): LiveData<List<TaskListNameAndCount>> {
        // TODO: Implement
        return MutableLiveData()
    }
}
