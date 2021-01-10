package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.howie.core.*
import com.example.howie.database.getDatabase
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.properties.Delegates

data class OptionsVisibility(
    val save: Boolean,
    val update: Boolean,
    val delete: Boolean,
    val archive: Boolean,
    val unarchive: Boolean,
    val moveToTaskList: Boolean,
    val schedule: Boolean
)

data class TaskFields(
    val name: String,
    val importance: Importance,
    val showDue: Boolean,
    val due: String,
    val showSnoozed: Boolean,
    val snoozed: String,
    val schedule: Schedule?
)

private data class NullableTask(
    val task: Task?
)

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val _repository: TasksRepository

    init {
        val database = getDatabase(application.applicationContext)
        _repository = TasksRepository(database.getTaskDao(), database.getTaskListDao())
    }

    var taskList by Delegates.notNull<TaskListIndex>()
        private set
    var taskIndex: TaskIndex? = null
        private set
    private val _taskList = MutableLiveData<TaskListIndex>()
    private val _taskIndex = MutableLiveData<TaskIndex?>()
    private val _taskCategory = MutableLiveData<TaskCategory?>()
    private val _task: LiveData<NullableTask> = CombinedLiveData(_taskList, _taskIndex).switchMap {
        liveData {
            if (it.second == null) {
                emit(NullableTask(null))
            } else {
                emit(NullableTask(_repository.getTask(it.first, it.second!!)))
            }
        }
    }
    val taskFields: LiveData<TaskFields> =
        CombinedLiveData(_task, _taskCategory).switchMap {
            liveData {
                val (nullableTask, category) = it
                val task = nullableTask.task
                val todayString = LocalDate.now().toString()
                if (task != null) {
                    val fields = TaskFields(
                        task.name,
                        task.importance,
                        task.due != null,
                        if (task.due == null) todayString else task.due.toString(),
                        task.snoozed != null,
                        if (task.snoozed == null) todayString else task.snoozed.toString(),
                        task.schedule
                    )
                    emit(fields)
                } else {
                    if (category == null) {
                        error("Set category in $TaskActivity")
                    }
                    val fields = TaskFields(
                        "",
                        if (category == TaskCategory.DO || category == TaskCategory.DECIDE) Importance.IMPORTANT else Importance.UNIMPORTANT,
                        category == TaskCategory.DO || category == TaskCategory.DELEGATE,
                        todayString,
                        false,
                        todayString,
                        null
                    )
                    emit(fields)
                }
            }
        }

    fun initialize(taskListIndex: TaskListIndex, task: TaskIndex?, taskCategory: TaskCategory?) =
        viewModelScope.launch {
            taskList = taskListIndex
            taskIndex = task
            _taskList.value = taskListIndex
            _taskIndex.value = task
            _taskCategory.value = taskCategory ?: TaskCategory.DO
        }

    fun updateTask(task: Task) = viewModelScope.launch {
        val success = _repository.updateTask(taskList, taskIndex!!, task)
        if (success) {
            callFinish()
        }
    }

    fun addTask(task: Task) = viewModelScope.launch {
        val sucess = _repository.addTask(taskList, task)
        if (sucess) {
            callFinish()
        }
    }

    fun doArchive() = viewModelScope.launch {
        TODO("Implement")
    }

    fun unarchive() = viewModelScope.launch {
        TODO("Implement")
    }

    fun deleteTask() = viewModelScope.launch {
        TODO("Implement")
    }

    val optionsVisibility: LiveData<OptionsVisibility> = _task.switchMap {
        liveData {
            val task = it.task
            val showArchive = task != null && task.archived == null
            val showUnarchive = task?.archived != null
            val options = OptionsVisibility(
                task == null,
                task != null,
                task != null,
                showArchive,
                showUnarchive,
                task != null,
                task != null
            )
            emit(options)
        }
    }

    private val _finishEvent = SingleLiveEvent<Boolean>()
    val finishEvent: LiveData<Boolean> by this::_finishEvent

    private fun callFinish() {
        _finishEvent.value = true
    }
}