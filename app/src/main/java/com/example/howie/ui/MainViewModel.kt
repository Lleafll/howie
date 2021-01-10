package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.howie.core.*
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

    var currentTaskList = TaskListIndex(0)
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

    fun setTaskList(taskList: TaskListIndex) = viewModelScope.launch {
        currentTaskList = taskList
        _currentTaskList.value = taskList
    }

    fun addTask(task: Task) = viewModelScope.launch {
        TODO("Implement")
    }

    fun doArchive(id: TaskIndex) = viewModelScope.launch {
        _repository.doArchive(currentTaskList, id)
        setTaskList(currentTaskList) // Force refresh of tasks
    }

    fun unarchive(id: TaskIndex) = viewModelScope.launch {
        _repository.unarchive(currentTaskList, id)
        setTaskList(currentTaskList) // Force refresh of tasks
    }

    fun delete(id: Int) = viewModelScope.launch {
        // TODO: Implement
    }

    fun addTaskList() = viewModelScope.launch {
        val newTaskListIndex = _repository.addTaskList()
        setTaskList(newTaskListIndex)
    }

    fun deleteTaskList(taskList: TaskListIndex) = viewModelScope.launch {
        _repository.deleteTaskList(taskList)
        setTaskList(TaskListIndex(0))
    }

    val taskListDrawerLabels: LiveData<List<String>> = _currentTaskList.switchMap {
        liveData {
            emit(_repository.getTaskListInformation().map { buildLabel(it) })
        }
    }

    fun snoozeToTomorrow(task: TaskIndex) = viewModelScope.launch {
        _repository.snoozeToTomorrow(currentTaskList, task)
        setTaskList(currentTaskList) // Force refresh of tasks
    }

    fun removeSnooze(index: TaskIndex) = viewModelScope.launch {
        _repository.removeSnooze(currentTaskList, index)
        setTaskList(currentTaskList) // Force refresh of tasks
    }

    fun reschedule(taskIndex: TaskIndex) = viewModelScope.launch {
        _repository.scheduleNext(currentTaskList, taskIndex)
        setTaskList(currentTaskList) // Force refresh of tasks
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