package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.howie.core.IndexedTask
import com.example.howie.core.TaskIndex
import com.example.howie.core.TaskListIndex
import com.example.howie.database.getDatabase
import kotlinx.coroutines.launch

class ArchiveViewModel(application: Application) : AndroidViewModel(application) {
    private val _repository: TasksRepository

    init {
        val database = getDatabase(application.applicationContext)
        _repository = TasksRepository(database.getTaskDao(), database.getTaskListDao())
    }

    lateinit var taskList: TaskListIndex
    private val _taskList = MutableLiveData<TaskListIndex>()
    val archive: LiveData<List<IndexedTask>> = _taskList.switchMap {
        liveData {
            emit(_repository.getArchive(it))
        }
    }

    fun setTaskList(taskListIndex: TaskListIndex) = viewModelScope.launch {
        taskList = taskListIndex
        _taskList.value = taskListIndex
    }

    fun unarchive(task: TaskIndex) = viewModelScope.launch {
        _repository.unarchive(taskList, task)
    }
}