package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.howie.core.*
import com.example.howie.database.getDatabase
import kotlinx.coroutines.CoroutineScope
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

private data class NullableTaskIndex(
    val index: TaskIndex?
)

private data class NullableTaskCategory(
    val category: TaskCategory?
)

class TaskViewModel(
    application: Application,
    private var _repository: TasksRepository,
    private val coroutineScope: CoroutineScope?
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        buildTaskRepository(application),
        null
    )

    private fun getCoroutineScope(): CoroutineScope = coroutineScope ?: viewModelScope

    var taskList by Delegates.notNull<TaskListIndex>()
        private set
    var taskIndex: TaskIndex? = null
        private set
    private val _taskList = MutableLiveData<TaskListIndex>()
    private val _taskIndex = MutableLiveData<NullableTaskIndex>()
    private val _taskCategory = MutableLiveData<NullableTaskCategory>()
    private val _task: LiveData<NullableTask> = CombinedLiveData(_taskList, _taskIndex).switchMap {
        liveData {
            if (it.second.index == null) {
                emit(NullableTask(null))
            } else {
                emit(NullableTask(_repository.getTask(it.first, it.second.index!!)))
            }
        }
    }
    val taskFields: LiveData<TaskFields> =
        CombinedLiveData(_task, _taskCategory).switchMap {
            liveData {
                val (nullableTask, nullableCategory) = it
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
                    val category = nullableCategory.category
                    val fields = if (category == null) {
                        TaskFields(
                            "",
                            Importance.IMPORTANT,
                            false,
                            todayString,
                            false,
                            todayString,
                            null
                        )
                    } else {
                        TaskFields(
                            "",
                            if (category == TaskCategory.DO || category == TaskCategory.DECIDE) Importance.IMPORTANT else Importance.UNIMPORTANT,
                            category == TaskCategory.DO || category == TaskCategory.DELEGATE,
                            todayString,
                            false,
                            todayString,
                            null
                        )
                    }
                    emit(fields)
                }
            }
        }

    fun initialize(taskListIndex: TaskListIndex, task: TaskIndex?, taskCategory: TaskCategory?) =
        getCoroutineScope().launch {
            taskList = taskListIndex
            taskIndex = task
            _taskList.value = taskListIndex
            _taskIndex.value = NullableTaskIndex(task)
            _taskCategory.value = NullableTaskCategory(taskCategory)
        }

    fun updateTask(task: Task) = getCoroutineScope().launch {
        val success = _repository.updateTask(taskList, taskIndex!!, task)
        if (success) {
            callFinish()
        }
    }

    fun addTask(task: Task) = getCoroutineScope().launch {
        val success = _repository.addTask(taskList, task)
        if (success) {
            callFinish()
        }
    }

    fun doArchive() = getCoroutineScope().launch {
        if (taskIndex == null) {
            error("doArchive cannot be called with a null taskIndex")
        }
        _repository.doArchive(taskList, taskIndex!!)
        callFinish()
    }

    fun unarchive() = getCoroutineScope().launch {
        if (taskIndex == null) {
            error("unarchive cannot be called with a null taskIndex")
        }
        _repository.unarchive(taskList, taskIndex!!)
        callFinish()
    }

    fun deleteTask() = getCoroutineScope().launch {
        if (taskIndex == null) {
            error("delete cannot be called with a null taskIndex")
        }
        _repository.deleteTask(taskList, taskIndex!!)
        callFinish()
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

private fun buildTaskRepository(application: Application): TasksRepository {
    val database = getDatabase(application.applicationContext)
    return TasksRepository(database.getTaskDao(), database.getTaskListDao())
}