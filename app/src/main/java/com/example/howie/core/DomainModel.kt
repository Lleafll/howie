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
    fun getTaskListNames() {
        // TODO: Implement
    }

    fun getTaskListInformation() {
        // TODO: Implement
    }

    fun getTaskCounts(taskList: Int) = TaskCounts(
        // TODO: Implement
        0, 0, 0, 0
    )

    fun add(taskList: Int, task: Task) {
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

    fun delete(id: Int) {
        // TODO: Implement
    }

    fun getCurrentTasks() {
        // TODO: Implement
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

        return true
    }
}