package com.lorenz.howie.ui

import com.lorenz.howie.core.UnarchivedTasks

data class UnarchivedTaskItemFields(
    val unsnoozed: List<TaskItemFields>,
    val snoozed: List<TaskItemFields>
)

fun UnarchivedTasks.toUnarchivedTaskItemFields(): UnarchivedTaskItemFields {
    return UnarchivedTaskItemFields(
        unsnoozed.map { it.toTaskItemFields() },
        snoozed.map { it.toTaskItemFields() }
    )
}