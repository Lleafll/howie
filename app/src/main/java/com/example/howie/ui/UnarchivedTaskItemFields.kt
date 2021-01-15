package com.example.howie.ui

import com.example.howie.core.UnarchivedTasks

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