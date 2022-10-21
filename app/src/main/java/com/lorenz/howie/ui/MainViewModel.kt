package com.lorenz.howie.ui

import android.app.Application
import androidx.lifecycle.*
import com.lorenz.howie.core.*
import com.lorenz.howie.database.getDatabase
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TabLabels(
    val label0: String,
    val label1: String,
    val label2: String,
    val label3: String
)

data class TaskListDrawerContent(
    val selectedIndex: Int,
    val labels: List<String>
)

class MainViewModel(
    private val _application: Application,
    private var _repository: TasksRepository
) :
    AndroidViewModel(_application) {

    constructor(application: Application) : this(application, buildTaskRepository(application))

    var currentTaskList: TaskListIndex? = null
        private set

    private val _currentTaskList = MutableLiveData(currentTaskList)

    val currentTaskListName: LiveData<String> = _currentTaskList.switchMap {
        liveData {
            emit(if (it == null) "All" else _repository.getTaskListName(it))
        }
    }

    val doTasks: LiveData<UnarchivedTaskItemFields> = _currentTaskList.switchMap {
        liveData {
            emit(_repository.getUnarchivedTasks(it, TaskCategory.DO).toUnarchivedTaskItemFields())
        }
    }

    val decideTasks: LiveData<UnarchivedTaskItemFields> = _currentTaskList.switchMap {
        liveData {
            emit(
                _repository.getUnarchivedTasks(it, TaskCategory.DECIDE).toUnarchivedTaskItemFields()
            )
        }
    }

    val delegateTasks: LiveData<UnarchivedTaskItemFields> = _currentTaskList.switchMap {
        liveData {
            emit(
                _repository.getUnarchivedTasks(it, TaskCategory.DELEGATE)
                    .toUnarchivedTaskItemFields()
            )
        }
    }

    val dropTasks: LiveData<UnarchivedTaskItemFields> = _currentTaskList.switchMap {
        liveData {
            emit(_repository.getUnarchivedTasks(it, TaskCategory.DROP).toUnarchivedTaskItemFields())
        }
    }

    fun selectTaskList(selected: Int) = viewModelScope.launch {
        currentTaskList = if (selected <= 0) null else TaskListIndex(selected - 1)
        _currentTaskList.value = currentTaskList
    }

    fun addTask(task: Task) = viewModelScope.launch {
        _repository.addTask(currentTaskList ?: TaskListIndex(0), task)
        refresh()
    }

    fun doArchive(id: TaskIndex) = viewModelScope.launch {
        _repository.doArchive(id, LocalDate.now())
        refresh()
        taskArchivedNotificationEvent.value = id
    }

    fun unarchive(id: TaskIndex) = viewModelScope.launch {
        _repository.unarchive(id)
        refresh()
    }

    fun addTaskList() = viewModelScope.launch {
        currentTaskList = _repository.addTaskList()
        _currentTaskList.value = currentTaskList
        refresh()
    }

    fun deleteCurrentTaskList() = viewModelScope.launch {
        currentTaskList?.let { _repository.deleteTaskList(it) }
        selectTaskList(0)
    }

    val taskListDrawerContent: LiveData<TaskListDrawerContent> = _currentTaskList.switchMap {
        liveData {
            emit(
                TaskListDrawerContent(
                    if (it == null) 0 else it.value + 1,
                    _repository.getTaskListInformation().map { buildLabel(it) }.toMutableList()
                        .apply { add(0, buildAllLabel(_repository.getTaskListInformation())) }
                )
            )
        }
    }

    fun snoozeToTomorrow(task: TaskIndex) = viewModelScope.launch {
        _repository.snoozeToTomorrow(task)
        refresh()
        taskSnoozedToTomorrowNotificationEvent.value = task
    }

    fun addSnooze(task: TaskIndex, snooze: LocalDate) = viewModelScope.launch {
        _repository.addSnooze(task, snooze)
        refresh()
    }

    fun removeSnooze(index: TaskIndex) = viewModelScope.launch {
        val oldSnooze = _repository.removeSnooze(index)
        refresh()
        if (oldSnooze != null) {
            snoozeRemovedNotificationEvent.value = Pair(index, oldSnooze)
        }
    }

    fun reschedule(taskIndex: TaskIndex) = viewModelScope.launch {
        _repository.scheduleNext(taskIndex)
        refresh()
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

    private fun refresh() {
        _currentTaskList.value = currentTaskList
    }

    fun forceRefresh() = viewModelScope.launch {
        _repository = buildTaskRepository(_application)
        refresh()
    }

    val taskArchivedNotificationEvent = SingleLiveEvent<TaskIndex>()
    val taskDeletedNotificationEvent = SingleLiveEvent<Task>()
    val taskSnoozedToTomorrowNotificationEvent = SingleLiveEvent<TaskIndex>()
    val snoozeRemovedNotificationEvent = SingleLiveEvent<Pair<TaskIndex, LocalDate>>()
    val taskScheduledNotificationEvent = SingleLiveEvent<Boolean>()

    fun renameTaskList(newName: String) = viewModelScope.launch {
        currentTaskList?.let { _repository.renameTaskList(it, newName) }
        forceRefresh()
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

private fun buildAllLabel(information: List<TaskListInformation>): String {
    var doCount = 0
    var decideCount = 0
    var delegateCount = 0
    var dropCount = 0
    for (info in information) {
        val counts = info.taskCounts
        doCount += counts.doCount
        decideCount += counts.decideCount
        delegateCount += counts.delegateCount
        dropCount += counts.dropCount
    }
    return "All (" +
            "${countToString(doCount)}/" +
            "${countToString(decideCount)}/" +
            "${countToString(delegateCount)}/" +
            "${countToString(dropCount)})"
}

private fun formatLabel(taskCount: Int, lowerText: String): String {
    val upperText = if (taskCount != 0) taskCount.toString() else "•"
    return "$upperText\n$lowerText"
}

private fun countToString(count: Int) = when (count) {
    0 -> "•"
    else -> count.toString()
}

private fun buildTaskRepository(application: Application): TasksRepository {
    val database = getDatabase(application.applicationContext)
    return TasksRepository(
        buildDefaultWidgetUpdater(application),
        database.getTaskDao(),
        database.getTaskListDao()
    )
}