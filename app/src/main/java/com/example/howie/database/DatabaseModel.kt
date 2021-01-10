package com.example.howie.database

import com.example.howie.TaskEntity
import com.example.howie.core.Task
import com.example.howie.core.TaskList

data class DatabaseModel(
    val taskEntities: List<TaskEntity>,
    val taskListEntities: List<TaskListEntity>
)

fun List<TaskList>.toDatabaseModel(): DatabaseModel {
    val taskListEntities =
        mapIndexed { index, taskList -> TaskListEntity(taskList.name, index.toLong()) }
    return DatabaseModel(listOf(), taskListEntities)
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