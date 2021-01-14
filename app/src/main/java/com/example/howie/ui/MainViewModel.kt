package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.howie.core.*
import com.example.howie.database.getDatabase
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TabLabels(
    val label0: String,
    val label1: String,
    val label2: String,
    val label3: String
)

class MainViewModel(
    private val _application: Application,
    private var _repository: TasksRepository
) :
    AndroidViewModel(_application) {

    constructor(application: Application) : this(application, buildTaskRepository(application))

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
        _repository.addTask(currentTaskList, task)
        setTaskList(currentTaskList) // Force refresh of tasks
    }

    fun doArchive(id: TaskIndex) = viewModelScope.launch {
        _repository.doArchive(currentTaskList, id)
        setTaskList(currentTaskList) // Force refresh of tasks
        taskArchivedNotificationEvent.value = id
    }

    fun unarchive(id: TaskIndex) = viewModelScope.launch {
        _repository.unarchive(currentTaskList, id)
        setTaskList(currentTaskList) // Force refresh of tasks
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
        taskSnoozedToTomorrowNotificationEvent.value = task
    }

    fun addSnooze(task: TaskIndex, snooze: LocalDate) = viewModelScope.launch {
        _repository.addSnooze(currentTaskList, task, snooze)
        setTaskList(currentTaskList) // Force refresh of tasks
    }

    fun removeSnooze(index: TaskIndex) = viewModelScope.launch {
        val oldSnooze = _repository.removeSnooze(currentTaskList, index)
        setTaskList(currentTaskList) // Force refresh of tasks
        if (oldSnooze != null) {
            snoozeRemovedNotificationEvent.value = Pair(index, oldSnooze)
        }
    }

    fun reschedule(taskIndex: TaskIndex) = viewModelScope.launch {
        _repository.scheduleNext(currentTaskList, taskIndex)
        setTaskList(currentTaskList) // Force refresh of tasks
        taskScheduledNotificationEvent.value = true
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

    fun forceRefresh() = viewModelScope.launch {
        _repository = buildTaskRepository(_application)
        setTaskList(currentTaskList)
    }

    val taskArchivedNotificationEvent = SingleLiveEvent<TaskIndex>()
    val taskDeletedNotificationEvent = SingleLiveEvent<Task>()
    val taskSnoozedToTomorrowNotificationEvent = SingleLiveEvent<TaskIndex>()
    val snoozeRemovedNotificationEvent = SingleLiveEvent<Pair<TaskIndex, LocalDate>>()
    val taskScheduledNotificationEvent = SingleLiveEvent<Boolean>()
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

private fun buildTaskRepository(application: Application): TasksRepository {
    val database = getDatabase(application.applicationContext)
    return TasksRepository(database.getTaskDao(), database.getTaskListDao())
}