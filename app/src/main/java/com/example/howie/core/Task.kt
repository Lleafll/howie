package com.example.howie.core

import com.example.howie.Task
import java.time.LocalDate

enum class Importance {
    IMPORTANT, UNIMPORTANT
}

data class Task(
    val name: String,
    val importance: Importance = Importance.IMPORTANT,
    val due: LocalDate? = null,
    val snoozed: LocalDate? = null,
    val schedule: Schedule? = null,
    val completed: LocalDate? = null,
    val archived: LocalDate? = null
)

enum class TaskCategory {
    DO, DECIDE, DELEGATE, DROP
}

fun taskCategory(task: Task): TaskCategory = if (task.importance == Importance.IMPORTANT) {
    if (task.due != null) {
        TaskCategory.DO
    } else {
        TaskCategory.DECIDE
    }
} else {
    if (task.due != null) {
        TaskCategory.DELEGATE
    } else {
        TaskCategory.DROP
    }
}

fun Task.scheduleNext(): Task? = if (schedule == null) {
    null
} else {
    val newDate = schedule.scheduleNext(LocalDate.now())
    val newTask = copy(due = newDate, snoozed = newDate)
    newTask.id = id
    newTask
}