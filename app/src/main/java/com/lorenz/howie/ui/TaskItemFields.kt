package com.lorenz.howie.ui

import com.lorenz.howie.core.IndexedTask
import com.lorenz.howie.core.TaskIndex
import com.lorenz.howie.core.isArchived
import com.lorenz.howie.core.isSnoozed
import java.time.LocalDate

data class TaskItemFields(
    val index: TaskIndex,
    val name: String,
    val due: String?,
    val snoozed: String?,
    val archived: String?,
    val snoozedToTomorrow: Boolean,
    val removeSnoozed: Boolean,
    val reschedule: String?,
    val archive: Boolean,
    val unarchive: Boolean
)

fun IndexedTask.toTaskItemFields(): TaskItemFields {
    val showSnoozeAction = !task.isSnoozed() && !task.isArchived()
    val showRemoveSnoozeAction = task.isSnoozed() && !task.isArchived()
    val showScheduleAction = task.schedule != null
    val showArchiveAction = task.archived == null
    return TaskItemFields(
        indexInTaskList,
        task.name,
        toString(task.due),
        toString(task.snoozed),
        toString(task.archived),
        showSnoozeAction,
        showRemoveSnoozeAction,
        if (showScheduleAction) task.schedule.toString() else null,
        showArchiveAction,
        !showArchiveAction
    )
}

private fun toString(date: LocalDate?): String? {
    return date?.toString()
}