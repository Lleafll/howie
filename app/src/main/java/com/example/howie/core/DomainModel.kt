package com.example.howie.core

data class UnarchivedTasks(
    val unsnoozed: List<Task>,
    val snoozed: List<Task>
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

class DomainModel(val taskLists: List<TaskList>) {
    fun getTaskListNames(): List<String> {
        TODO("Implement")
    }

    fun getTaskListInformation(taskList: Int): TaskListInformation {
        TODO("Implement")
    }

    fun getTaskListInformation(): List<TaskListInformation> {
        return taskLists.map { TaskListInformation(it.name, countTasks(it.tasks)) }
    }

    fun getTaskCounts(taskList: Int) = TaskCounts(
        // TODO: Implement
        0, 0, 0, 0
    )

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
        val unArchivedTasks = filterUnarchivedTasks(taskLists[taskList].tasks)
        val categoryTasks = filterCategory(unArchivedTasks, category)
        val partitionedTasks = categoryTasks.partition { it.snoozed == null }
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
        TODO("Not yet implemented")
    }

    fun moveTaskFromListToList(taskId: Int, fromTaskList: Int, toList: Int): Boolean {
        TODO("Implement")
    }
}

private fun filterUnarchivedTasks(tasks: Iterable<Task>) = tasks.filter { it.archived == null }

private fun countTasks(tasks: Iterable<Task>): TaskCounts {
    return filterUnarchivedTasks(tasks).let {
        TaskCounts(
            count(it, TaskCategory.DO),
            count(it, TaskCategory.DECIDE),
            count(it, TaskCategory.DELEGATE),
            count(it, TaskCategory.DROP)
        )
    }
}

private fun count(tasks: Iterable<Task>, category: TaskCategory) =
    tasks.count { taskCategory(it) == category }

private fun filterCategory(tasks: Iterable<Task>, category: TaskCategory) =
    tasks.filter { taskCategory(it) == category }