package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.howie.core.IndexedTask
import com.example.howie.core.TaskIndex
import com.example.howie.core.TaskListIndex
import com.example.howie.database.getDatabase
import kotlinx.coroutines.launch

class ArchiveViewModel(private val _application: Application) : AndroidViewModel(_application) {
    private var _repository = buildTaskRepository(_application)

    lateinit var currentTaskList: TaskListIndex
    private val _taskList = MutableLiveData<TaskListIndex>()
    val archive: LiveData<List<IndexedTask>> = _taskList.switchMap {
        liveData {
            emit(_repository.getArchive(it))
        }
    }

    fun setTaskList(taskListIndex: TaskListIndex) = viewModelScope.launch {
        currentTaskList = taskListIndex
        _taskList.value = taskListIndex
    }

    fun unarchive(task: TaskIndex) = viewModelScope.launch {
        _repository.unarchive(currentTaskList, task)
        setTaskList(currentTaskList)
    }

    fun forceRefresh() = viewModelScope.launch {
        _repository = buildTaskRepository(_application)
        setTaskList(currentTaskList)
    }
}

private fun buildTaskRepository(application: Application): TasksRepository {
    val database = getDatabase(application.applicationContext)
    return TasksRepository(database.getTaskDao(), database.getTaskListDao())
}