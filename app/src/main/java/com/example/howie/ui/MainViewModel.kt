package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.howie.core.Task
import com.example.howie.core.TaskListInformation
import com.example.howie.database.getDatabase
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

const val HOWIE_SHARED_PREFERENCES_KEY = "howie_default_shared_preferences"

class MainViewModel(application: Application, private val _repository: TasksRepository) :
    AndroidViewModel(application) {

    constructor(application: Application) : this(application, {
        val database = getDatabase(application.applicationContext)
        TasksRepository(database.getTaskDao(), database.getTaskListDao())
    }())

    var currentTaskList by Delegates.notNull<Int>()
        private set
    private val _currentTaskListChanged = MutableLiveData<Int>()
    private val _currentTaskListName = MutableLiveData<String>()
    val currentTaskListName: LiveData<String> by this::_currentTaskListName

    fun setTaskList(taskList: Int) {
        currentTaskList = taskList
        _currentTaskListChanged.value = taskList
    }

    fun addTask(task: Task) = viewModelScope.launch {
        // TODO: Implement
    }

    fun doArchive(id: Int) = viewModelScope.launch {
        _repository.doArchive(currentTaskList, id)
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

    val taskListDrawerLabels = liveData {
        emit(_repository.getTaskListInformation().map { buildLabel(it) })
    }
}

private fun buildLabel(information: TaskListInformation): String {
    val taskCounts = information.taskCounts
    return "${information.name} (" +
            "${countToString(taskCounts.doCount)}/" +
            "${countToString(taskCounts.decideCount)}/" +
            "${countToString(taskCounts.delegateCount)}/" +
            "${countToString(taskCounts.dropCount)})"
}

private fun countToString(count: Int) = when (count) {
    0 -> "✓"
    else -> count.toString()
}