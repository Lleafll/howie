package com.lorenz.howie.ui

import android.app.Application
import androidx.lifecycle.*
import com.lorenz.howie.core.Task
import com.lorenz.howie.core.TaskIndex
import com.lorenz.howie.core.TaskListIndex
import com.lorenz.howie.database.getDatabase
import kotlinx.coroutines.launch
import java.time.LocalDate

class ArchiveViewModel(private val _application: Application) : AndroidViewModel(_application) {
    private var _repository = buildTaskRepository(_application)

    lateinit var currentTaskList: TaskListIndex
    private val _taskList = MutableLiveData<TaskListIndex>()
    val archive: LiveData<List<TaskItemFields>> = _taskList.switchMap {
        liveData {
            emit(_repository.getArchive(it).map { it.toTaskItemFields() })
        }
    }

    fun setTaskList(taskListIndex: TaskListIndex) = viewModelScope.launch {
        currentTaskList = taskListIndex
        _taskList.value = taskListIndex
    }

    fun unarchive(task: TaskIndex) = viewModelScope.launch {
        val oldArchiveDate = _repository.unarchive(task)
        setTaskList(currentTaskList)
        if (oldArchiveDate != null) {
            taskUnarchivedNotificationEvent.value = Pair(task, oldArchiveDate)
        }
    }

    fun doArchive(id: TaskIndex, date: LocalDate) = viewModelScope.launch {
        _repository.doArchive(id, date)
        setTaskList(currentTaskList) // Force refresh of tasks
    }

    fun forceRefresh() = viewModelScope.launch {
        _repository = buildTaskRepository(_application)
        setTaskList(currentTaskList)
    }

    fun addTask(task: Task) = viewModelScope.launch {
        _repository.addTask(currentTaskList, task)
        setTaskList(currentTaskList) // Force refresh of tasks
    }

    val taskUnarchivedNotificationEvent = SingleLiveEvent<Pair<TaskIndex, LocalDate>>()
    val taskDeletedNotificationEvent = SingleLiveEvent<Task>()
    val title: LiveData<String> = _taskList.switchMap {
        liveData {
            emit("Archive: ${_repository.getTaskListName(it)}")
        }
    }
}

private fun buildTaskRepository(application: Application): TasksRepository {
    val database = getDatabase(application.applicationContext)
    return TasksRepository(
        buildDefaultWidgetUpdater(application),
        database.getTaskDao(),
        database.getTaskListDao()
    )
}