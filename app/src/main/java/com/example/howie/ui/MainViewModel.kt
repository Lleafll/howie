package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.howie.core.Task
import com.example.howie.core.TaskCategory
import com.example.howie.core.TaskListInformation
import com.example.howie.core.UnarchivedTasks
import com.example.howie.database.getDatabase
import kotlinx.coroutines.launch

const val HOWIE_SHARED_PREFERENCES_KEY = "howie_default_shared_preferences"

class MainViewModel(application: Application, private val _repository: TasksRepository) :
    AndroidViewModel(application) {

    constructor(application: Application) : this(application, {
        val database = getDatabase(application.applicationContext)
        TasksRepository(database.getTaskDao(), database.getTaskListDao())
    }())

    var currentTaskList = 0
        private set
    private var _currentTaskCategoryValue = TaskCategory.DO

    private val _currentTaskCategory = MutableLiveData(_currentTaskCategoryValue)
    private val _currentTaskList = MutableLiveData(currentTaskList)

    private val _currentTaskListName = MutableLiveData<String>()
    val currentTaskListName: LiveData<String> by this::_currentTaskListName

    val tasks: LiveData<UnarchivedTasks> =
        CombinedLiveData(_currentTaskList, _currentTaskCategory).switchMap {
            liveData {
                emit(_repository.getUnarchivedTasks(it.first, it.second))
            }
        }

    fun setTaskList(taskList: Int) = viewModelScope.launch {
        currentTaskList = taskList
        _currentTaskList.value = taskList
    }

    fun setTaskCategory(category: TaskCategory) = viewModelScope.launch {
        _currentTaskCategory.value = category
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

    fun snoozeToTomorrow(task: Int) {
        TODO("Implement")
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