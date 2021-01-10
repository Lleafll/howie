package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.howie.core.Task
import com.example.howie.core.TaskCategory
import com.example.howie.core.TaskListInformation
import com.example.howie.core.UnarchivedTasks
import com.example.howie.database.getDatabase
import kotlinx.coroutines.launch

data class TabLabels(
    val label0: String,
    val label1: String,
    val label2: String,
    val label3: String
)

class MainViewModel(application: Application, private val _repository: TasksRepository) :
    AndroidViewModel(application) {

    constructor(application: Application) : this(application, {
        val database = getDatabase(application.applicationContext)
        TasksRepository(database.getTaskDao(), database.getTaskListDao())
    }())

    var currentTaskList = 0
        private set

    private val _currentTaskList = MutableLiveData(currentTaskList)

    val currentTaskListName: LiveData<String> = _currentTaskList.switchMap {
        liveData {
            emit(_repository.getTaskListName(it))
        }
    }

    val doTasks: LiveData<UnarchivedTasks> = _currentTaskList.switchMap {
        liveData {
            emit(_repository.getUnarchivedTasks(it, TaskCategory.DO))
        }
    }

    val decideTasks: LiveData<UnarchivedTasks> = _currentTaskList.switchMap {
        liveData {
            emit(_repository.getUnarchivedTasks(it, TaskCategory.DECIDE))
        }
    }

    val delegateTasks: LiveData<UnarchivedTasks> = _currentTaskList.switchMap {
        liveData {
            emit(_repository.getUnarchivedTasks(it, TaskCategory.DELEGATE))
        }
    }

    val dropTasks: LiveData<UnarchivedTasks> = _currentTaskList.switchMap {
        liveData {
            emit(_repository.getUnarchivedTasks(it, TaskCategory.DROP))
        }
    }

    fun setTaskList(taskList: Int) = viewModelScope.launch {
        currentTaskList = taskList
        _currentTaskList.value = taskList
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

    fun addTaskList() = viewModelScope.launch {
        val newTaskListIndex = _repository.addTaskList()
        setTaskList(newTaskListIndex)
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

    val tabLabels: LiveData<TabLabels> = _currentTaskList.switchMap {
        liveData {
            val taskCounts = _repository.getTaskCounts(it)
            val labels = TabLabels(
                formatLabel(taskCounts.doCount, "Do"),
                formatLabel(taskCounts.decideCount, "Decide"),
                formatLabel(taskCounts.delegateCount, "Delegate"),
                formatLabel(taskCounts.dropCount, "Drop")
            )
            emit(labels)
        }
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

private fun formatLabel(taskCount: Int, lowerText: String): String {
    val upperText = if (taskCount != 0) taskCount.toString() else "✓"
    return "$upperText\n$lowerText"
}

private fun countToString(count: Int) = when (count) {
    0 -> "✓"
    else -> count.toString()
}