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

class DomainModel(
    val tasks: List<Task>,
    val taskLists: List<TaskList>,
    var currentTaskListId: Long
) {
    fun setCurrenTaskList(id: Long) {
        // TODO: Implement
    }

    fun add(task: Task) {
        // TODO: Implement
    }

    fun update(task: Task) {
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

    fun getCurrentArchivedTasks() {
        // TODO: Implement
    }

    fun getCurrentTasks(category: TaskCategory, snoozed: Boolean) = CategorizedTasks(
        // TODO: Implement
        UnarchivedTasks(listOf(), listOf()),
        UnarchivedTasks(listOf(), listOf()),
        UnarchivedTasks(listOf(), listOf()),
        UnarchivedTasks(listOf(), listOf())
    )

    fun getArchive() {
        // TODO: Implement
    }

    fun deleteTaskList(taskListId: Long): Boolean {
        if (taskListId == 0L) {
            return false
        }
        currentTaskListId = 0L
        // TODO: Implement logic
        return true
    }
}