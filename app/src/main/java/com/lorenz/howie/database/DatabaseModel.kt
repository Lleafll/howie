package com.lorenz.howie.database

import com.example.howie.TaskEntity
import com.lorenz.howie.core.Task
import com.lorenz.howie.core.TaskList

data class DatabaseModel(
    val taskEntities: List<TaskEntity>,
    val taskListEntities: List<TaskListEntity>
)

fun List<TaskList>.toDatabaseModel(): DatabaseModel {
    val taskListEntities =
        mapIndexed { index, taskList -> TaskListEntity(taskList.name, index.toLong()) }
    var taskIndex = 0
    val taskEntities = mapIndexed { index, taskList ->
        val taskListIndex = index.toLong()
        taskList.tasks.map { task ->
            TaskEntity(
                task.name,
                taskListIndex,
                task.importance,
                task.due,
                task.snoozed,
                task.schedule,
                null,
                task.archived,
                taskIndex++
            )
        }
    }.flatten()
    return DatabaseModel(taskEntities, taskListEntities)
}

fun DatabaseModel.toDomainModel(): List<TaskList> {
    return taskListEntities.map { taskListEntity ->
        TaskList(
            taskListEntity.name,
            taskEntities.filter { it.taskListId == taskListEntity.id }.map { it.toTask() }
                .toMutableList()
        )
    }
}

private fun TaskEntity.toTask(): Task {
    return Task(name, importance, due, snoozed, schedule, archived)
}