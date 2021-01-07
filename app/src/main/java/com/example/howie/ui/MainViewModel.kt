package com.example.howie.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.example.howie.Task
import com.example.howie.database.TaskList
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
        val database = TasksDatabaseSingleton.getDatabase(application.applicationContext)
        val preferences =
            application.getSharedPreferences(HOWIE_SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        TasksRepository(database.getTaskDao(), database.getTaskListDao(), preferences)
    }())

    val tasks = repository.tasks
    val taskLists = repository.taskLists
    val currentTaskList = repository.currentTaskList
    val currentTaskListId = repository.currentTaskListId

    fun add(task: Task) = viewModelScope.launch {
        repository.add(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun doArchive(id: Int) = viewModelScope.launch {
        repository.doArchive(id)
    }

    fun unarchive(id: Int) = viewModelScope.launch {
        repository.unarchive(id)
    }

    fun delete(id: Int) = viewModelScope.launch {
        repository.delete(id)
    }

    fun switchToTaskList(newTaskListId: Long) = repository.switchToTaskList(newTaskListId)

    fun addTaskList(name: String) = viewModelScope.launch {
        repository.addTaskList(name)
    }

    fun deleteTaskList(taskListId: Long) = viewModelScope.launch {
        repository.deleteTaskList(taskListId)
    }

    fun getTaskListNamesAndCounts(): LiveData<List<TaskListNameAndCount>> {
        val liveData = MediatorLiveData<List<TaskListNameAndCount>>()
        var taskLists: List<TaskList>? = null
        var tasks: List<Task>? = null
        val setLiveData = {
            if (taskLists != null && tasks != null) {
                liveData.value = getTaskListNameAndCounts(tasks!!, taskLists!!)
            }
        }
        liveData.addSource(repository.taskLists) {
            taskLists = it
            setLiveData()
        }
        liveData.addSource(repository.tasks) {
            tasks = it
            setLiveData()
        }
        return liveData
    }

    companion object {
        private var instance: MainViewModel? = null

        @Deprecated("Use a proper ViewModel")
        fun getInstance(application: Application): MainViewModel {
            if (instance == null) {
                instance = MainViewModel(application)
            }
            return instance!!
        }
    }
}

private fun getTaskListNameAndCounts(
    tasks: List<Task>,
    tasksLists: List<TaskList>
): List<TaskListNameAndCount> {
    return tasksLists.map { taskList ->
        TaskListNameAndCount(
            taskList.id,
            taskList.name,
            getTaskCounts(tasks.filter { task ->
                task.archived == null
            }, taskList.id)
        )
    }
}

private fun getTaskCounts(tasks: List<Task>, taskListId: Long) =
    getTaskCounts(tasks.filter { it.taskListId == taskListId })

private fun getTaskCounts(tasks: List<Task>) = TaskCounts(
    count(tasks, TaskCategory.DO),
    count(tasks, TaskCategory.DECIDE),
    count(tasks, TaskCategory.DELEGATE),
    count(tasks, TaskCategory.DROP),
)

private fun count(tasks: List<Task>, category: TaskCategory) =
    tasks.count { taskCategory(it) == category }