package com.example.howie.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
data class TaskListIndex(
    val value: Int
) : Parcelable

@Parcelize
data class TaskIndex(
    val value: Int
) : Parcelable

data class IndexedTask(
    val indexInTaskList: TaskIndex,
    val task: Task
)

data class UnarchivedTasks(
    val unsnoozed: List<IndexedTask>,
    val snoozed: List<IndexedTask>
)

data class TaskCounts(
    val doCount: Int,
    val decideCount: Int,
    val delegateCount: Int,
    val dropCount: Int
)

data class TaskListInformation(
    val name: String,
    val taskCounts: TaskCounts
)

class DomainModel(initialTaskLists: List<TaskList>) {
    private val _taskLists: MutableList<TaskList> = if (initialTaskLists.isEmpty()) {
        mutableListOf(TaskList("Tasks", mutableListOf()))
    } else {
        initialTaskLists.toMutableList()
    }
    val taskLists: List<TaskList> by this::_taskLists

    fun getTaskListNames(): List<String> {
        return taskLists.map { it.name }
    }

    fun getTaskListInformation(taskList: TaskListIndex): TaskListInformation {
        val taskList = taskLists[taskList.value]
        return TaskListInformation(
            taskList.name,
            countUnarchivedUnsnoozedTasks(taskList.tasks)
        )
    }

    fun getTaskListInformation(): List<TaskListInformation> {
        return taskLists.map {
            TaskListInformation(
                it.name,
                countUnarchivedUnsnoozedTasks(it.tasks)
            )
        }
    }

    fun getTaskCounts(taskList: TaskListIndex): TaskCounts {
        return countUnarchivedUnsnoozedTasks(taskLists[taskList.value].tasks)
    }

    fun addTask(taskList: TaskListIndex, task: Task): Boolean {
        return _taskLists[taskList.value].tasks.add(task)
    }

    fun doArchive(taskList: TaskListIndex, task: TaskIndex) {
        val taskObject = _taskLists[taskList.value].tasks[task.value]
        _taskLists[taskList.value].tasks[task.value] = taskObject.copy(archived = LocalDate.now())
    }

    fun unarchive(taskList: TaskListIndex, task: TaskIndex) {
        val taskObject = _taskLists[taskList.value].tasks[task.value]
        _taskLists[taskList.value].tasks[task.value] = taskObject.copy(archived = null)
    }

    fun getUnarchivedTasks(taskList: TaskListIndex, category: TaskCategory): UnarchivedTasks {
        val unArchivedTasks = filterUnarchivedTasksToIndexedTask(taskLists[taskList.value].tasks)
        val categoryTasks = filterCategory(unArchivedTasks, category)
        val partitionedTasks = categoryTasks.partition { it.task.isSnoozed() }
        return UnarchivedTasks(partitionedTasks.second, partitionedTasks.first)
    }

    fun getArchive(taskList: TaskListIndex): List<IndexedTask> {
        return filterArchivedTasksToIndexTask(taskLists[taskList.value].tasks)
    }

    fun deleteTaskList(taskList: TaskListIndex): Boolean {
        return if (taskLists.size <= 1) {
            false
        } else {
            _taskLists.removeAt(taskList.value)
            true
        }
    }

    fun getTask(taskListIndex: TaskListIndex, taskIndex: TaskIndex): Task {
        return taskLists[taskListIndex.value].tasks[taskIndex.value]
    }

    fun moveTaskFromListToList(
        taskId: TaskIndex,
        fromTaskList: TaskListIndex,
        toList: TaskListIndex
    ) {
        val task = taskLists[fromTaskList.value].tasks.removeAt(taskId.value)
        taskLists[toList.value].tasks.add(task)
    }

    fun getTaskListName(taskList: TaskListIndex): String {
        return taskLists[taskList.value].name
    }

    fun addTaskList(): TaskListIndex {
        _taskLists.add(TaskList("New Task List", mutableListOf()))
        return TaskListIndex(_taskLists.size - 1)
    }

    fun snoozeToTomorrow(taskList: TaskListIndex, task: TaskIndex) {
        val tomorrow = LocalDate.now().plusDays(1)
        val taskObject = _taskLists[taskList.value].tasks[task.value]
        _taskLists[taskList.value].tasks[task.value] = taskObject.copy(snoozed = tomorrow)
    }

    fun removeSnooze(taskList: TaskListIndex, task: TaskIndex): LocalDate? {
        val taskObject = _taskLists[taskList.value].tasks[task.value]
        _taskLists[taskList.value].tasks[task.value] = taskObject.copy(snoozed = null)
        return taskObject.snoozed
    }

    fun scheduleNext(taskList: TaskListIndex, task: TaskIndex) {
        val taskObject = _taskLists[taskList.value].tasks[task.value]
        val nextTask = taskObject.scheduleNext()
        if (nextTask != null) {
            _taskLists[taskList.value].tasks[task.value] = nextTask
        }
    }

    fun updateTask(taskList: TaskListIndex, taskIndex: TaskIndex, task: Task): Boolean {
        taskLists[taskList.value].tasks[taskIndex.value] = task
        return true
    }

    fun deleteTask(taskList: TaskListIndex, task: TaskIndex) {
        taskLists[taskList.value].tasks.removeAt(task.value)
    }

    fun renameTaskList(taskListId: TaskListIndex, newName: String) {
        val taskList = _taskLists[taskListId.value]
        _taskLists[taskListId.value] = taskList.copy(name = newName)
    }

    fun addSnooze(taskList: TaskListIndex, task: TaskIndex, snooze: LocalDate) {
        val taskObject = _taskLists[taskList.value].tasks[task.value]
        _taskLists[taskList.value].tasks[task.value] = taskObject.copy(snoozed = snooze)
    }
}

private fun filterArchivedTasksToIndexTask(tasks: Iterable<Task>) =
    tasks.withIndex()
        .filter { (_, task) -> task.archived != null }
        .map { (i, task) -> IndexedTask(TaskIndex(i), task) }

private fun filterUnarchivedTasksToIndexedTask(tasks: Iterable<Task>) =
    tasks.withIndex()
        .filter { (_, task) -> task.archived == null }
        .map { (i, task) -> IndexedTask(TaskIndex(i), task) }

private fun filterUnarchivedTasks(tasks: Iterable<Task>) = tasks.filter { it.archived == null }

private fun filterUnarchivedUnsnoozedTasks(tasks: Iterable<Task>) =
    filterUnarchivedTasks(tasks).filter { !it.isSnoozed() }

private fun countUnarchivedUnsnoozedTasks(tasks: Iterable<Task>): TaskCounts {
    return filterUnarchivedUnsnoozedTasks(tasks).let {
        TaskCounts(
            count(it, TaskCategory.DO),
            count(it, TaskCategory.DECIDE),
            count(it, TaskCategory.DELEGATE),
            count(it, TaskCategory.DROP)
        )
    }
}

private fun count(tasks: Iterable<Task>, category: TaskCategory) =
    tasks.count { it.category() == category }

private fun filterCategory(tasks: Iterable<IndexedTask>, category: TaskCategory) =
    tasks.filter { it.task.category() == category }