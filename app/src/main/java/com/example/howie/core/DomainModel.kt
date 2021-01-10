package com.example.howie.core

data class IndexedTask(
    val indexInTaskList: Int,
    val task: Task
)

data class UnarchivedTasks(
    val unsnoozed: List<IndexedTask>,
    val snoozed: List<IndexedTask>
)

data class CategorizedTasks(
    val doTasks: UnarchivedTasks,
    val decideTasks: UnarchivedTasks,
    val delegateTasks: UnarchivedTasks,
    val dropTasks: UnarchivedTasks
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
        mutableListOf(TaskList("Tasks", listOf()))
    } else {
        initialTaskLists.toMutableList()
    }
    val taskLists: List<TaskList> by this::_taskLists

    fun getTaskListNames(): List<String> {
        TODO("Implement")
    }

    fun getTaskListInformation(taskList: Int): TaskListInformation {
        TODO("Implement")
    }

    fun getTaskListInformation(): List<TaskListInformation> {
        return taskLists.map {
            TaskListInformation(
                it.name,
                countUnarchivedUnsnoozedTasks(it.tasks)
            )
        }
    }

    fun getTaskCounts(taskList: Int): TaskCounts {
        return countUnarchivedUnsnoozedTasks(taskLists[taskList].tasks)
    }

    fun addTask(taskList: Int, task: Task) {
        // TODO: Implement
    }

    fun update(taskList: Int, task: Task) {
        // TODO: Implement
    }

    fun doArchive(id: Int) {
        // TODO: Implement
    }

    fun unarchive(id: Int) {
        // TODO: Implement
    }

    fun deleteTask(id: Int) {
        // TODO: Implement
    }

    fun getCurrentTasks() {
        // TODO: Implement
    }

    fun getUnarchivedTasks(taskList: Int, category: TaskCategory): UnarchivedTasks {
        val unArchivedTasks = filterUnarchivedTasksToIndexedTask(taskLists[taskList].tasks)
        val categoryTasks = filterCategory(unArchivedTasks, category)
        val partitionedTasks = categoryTasks.partition { it.task.snoozed == null }
        return UnarchivedTasks(partitionedTasks.first, partitionedTasks.second)
    }

    fun getCurrentTasks(taskList: Int) = CategorizedTasks(
        // TODO: Implement
        UnarchivedTasks(listOf(), listOf()),
        UnarchivedTasks(listOf(), listOf()),
        UnarchivedTasks(listOf(), listOf()),
        UnarchivedTasks(listOf(), listOf())
    )

    fun getArchive(taskList: Int) {
        // TODO: Implement
    }

    fun deleteTaskList(taskList: Int): Boolean {
        if (taskLists.size <= 1) {
            return false
        }
        // TODO: Implement
        return true
    }

    fun getTask(taskListIndex: Int, taskIndex: Int): Task {
        return taskLists[taskListIndex].tasks[taskIndex]
    }

    fun moveTaskFromListToList(taskId: Int, fromTaskList: Int, toList: Int): Boolean {
        TODO("Implement")
    }

    fun getTaskListName(taskList: Int): String {
        return taskLists[taskList].name
    }
}

private fun filterUnarchivedTasksToIndexedTask(tasks: Iterable<Task>): List<IndexedTask> =
    tasks.withIndex()
        .filter { (i, task) -> task.archived == null }
        .map { (i, task) -> IndexedTask(i, task) }

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