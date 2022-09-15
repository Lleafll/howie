package com.lorenz.howie.database

import com.lorenz.howie.core.Task
import com.lorenz.howie.core.TaskList

data class DatabaseModel(
    val taskEntities: List<TaskEntity>,
    val taskListEntities: List<TaskListEntity>
)

fun List<TaskList>.toDatabaseModel(): DatabaseModel {
    val modifiable = filter { it.canBeModified }
    val taskListEntities =
        modifiable.mapIndexed { index, taskList ->
            TaskListEntity(
                taskList.name,
                index.toLong()
            )
        }
    var taskIndex = 0
    val taskEntities = modifiable.mapIndexed { index, taskList ->
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
    val model = taskListEntities.map { taskListEntity ->
        TaskList(
            taskListEntity.name,
            taskEntities.filter { it.taskListId == taskListEntity.id }.map { it.toTask() }
                .toMutableList()
        )
    }.toMutableList()
    model.add(0, TaskList("All", taskEntities.map { it.toTask() }.toMutableList(), false))
    return model
}

private fun TaskEntity.toTask(): Task {
    return Task(name, importance, due, snoozed, schedule, archived)
}