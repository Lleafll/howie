package com.lorenz.howie.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class TaskListIndex(
    val value: Int
) : Parcelable

@Parcelize
data class TaskIndex(
    val list: TaskListIndex,
    val task: Int
) : Parcelable

data class IndexedTask(
    val index: TaskIndex,
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

    fun getTaskListInformation(taskListIndex: TaskListIndex): TaskListInformation {
        val taskList = taskLists[taskListIndex.value]
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

    fun getTaskCounts(taskList: TaskListIndex?): TaskCounts {
        return if (taskList != null) {
            countUnarchivedUnsnoozedTasks(taskLists[taskList.value].tasks)
        } else {
            TaskCounts(0, 0, 0, 0)
        }
    }

    fun addTask(taskList: TaskListIndex, task: Task): Boolean {
        return _taskLists[taskList.value].tasks.add(task)
    }

    fun doArchive(task: TaskIndex, date: LocalDate) {
        val taskObject = _taskLists[task.list.value].tasks[task.task]
        _taskLists[task.list.value].tasks[task.task] = taskObject.copy(archived = date)
    }

    fun unarchive(task: TaskIndex): LocalDate? {
        val taskObject = _taskLists[task.list.value].tasks[task.task]
        _taskLists[task.list.value].tasks[task.task] = taskObject.copy(archived = null)
        return taskObject.archived
    }

    fun getUnarchivedTasks(taskList: TaskListIndex?, category: TaskCategory): UnarchivedTasks {
        val unArchivedTasks = if (taskList == null) listOf() else
            filterUnarchivedTasksToIndexedTask(taskLists[taskList.value].tasks, taskList)
        val categoryTasks = filterCategory(unArchivedTasks, category)
        val partitionedTasks = categoryTasks.partition { it.task.isSnoozed() }
        return UnarchivedTasks(
            partitionedTasks.second.sortedBy { it.task.due },
            partitionedTasks.first.sortedBy { it.task.snoozed }
        )
    }

    fun getArchive(taskList: TaskListIndex): List<IndexedTask> {
        return filterArchivedTasksToIndexTask(taskLists[taskList.value].tasks, taskList)
    }

    fun deleteTaskList(taskList: TaskListIndex): Boolean {
        return if (taskLists.size <= 1) {
            false
        } else {
            _taskLists.removeAt(taskList.value)
            true
        }
    }

    fun getTask(taskIndex: TaskIndex): Task {
        return taskLists[taskIndex.list.value].tasks[taskIndex.task]
    }

    fun moveTaskFromListToList(
        taskId: TaskIndex,
        toList: TaskListIndex
    ) {
        val task = taskLists[taskId.list.value].tasks.removeAt(taskId.task)
        taskLists[toList.value].tasks.add(task)
    }

    fun getTaskListName(taskList: TaskListIndex): String {
        return taskLists[taskList.value].name
    }

    fun addTaskList(): TaskListIndex {
        _taskLists.add(TaskList("New Task List", mutableListOf()))
        return TaskListIndex(_taskLists.size - 1)
    }

    fun snoozeToTomorrow(task: TaskIndex) {
        val tomorrow = LocalDate.now().plusDays(1)
        val taskObject = _taskLists[task.list.value].tasks[task.task]
        _taskLists[task.list.value].tasks[task.task] = taskObject.copy(snoozed = tomorrow)
    }

    fun removeSnooze(task: TaskIndex): LocalDate? {
        val taskObject = _taskLists[task.list.value].tasks[task.task]
        _taskLists[task.list.value].tasks[task.task] = taskObject.copy(snoozed = null)
        return taskObject.snoozed
    }

    fun scheduleNext(task: TaskIndex) {
        val taskObject = _taskLists[task.list.value].tasks[task.task]
        val nextTask = taskObject.scheduleNext()
        if (nextTask != null) {
            _taskLists[task.list.value].tasks[task.task] = nextTask
        }
    }

    fun updateTask(taskIndex: TaskIndex, task: Task): Boolean {
        taskLists[taskIndex.list.value].tasks[taskIndex.task] = task
        return true
    }

    fun deleteTask(task: TaskIndex): Task {
        return taskLists[task.list.value].tasks.removeAt(task.task)
    }

    fun renameTaskList(taskListId: TaskListIndex, newName: String) {
        val taskList = _taskLists[taskListId.value]
        _taskLists[taskListId.value] = taskList.copy(name = newName)
    }

    fun addSnooze(task: TaskIndex, snooze: LocalDate) {
        val taskObject = _taskLists[task.list.value].tasks[task.task]
        _taskLists[task.list.value].tasks[task.task] = taskObject.copy(snoozed = snooze)
    }
}

private fun filterArchivedTasksToIndexTask(tasks: Iterable<Task>, taskList: TaskListIndex) =
    tasks.withIndex()
        .filter { (_, task) -> task.archived != null }
        .map { (i, task) -> IndexedTask(TaskIndex(taskList, i), task) }

private fun filterUnarchivedTasksToIndexedTask(tasks: Iterable<Task>, taskList: TaskListIndex) =
    tasks.withIndex()
        .filter { (_, task) -> task.archived == null }
        .map { (i, task) -> IndexedTask(TaskIndex(taskList, i), task) }

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