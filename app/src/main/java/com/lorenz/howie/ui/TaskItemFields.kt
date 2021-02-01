package com.lorenz.howie.ui

import com.lorenz.howie.core.IndexedTask
import com.lorenz.howie.core.TaskIndex
import com.lorenz.howie.core.isArchived
import com.lorenz.howie.core.isSnoozed

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
    val snoozed: String? = if (task.isSnoozed()) {
        task.snoozed.toString()
    } else {
        null
    }
    return TaskItemFields(
        indexInTaskList,
        task.name,
        task.due?.toString(),
        snoozed,
        task.archived?.toString(),
        showSnoozeAction,
        showRemoveSnoozeAction,
        if (showScheduleAction) task.schedule.toString() else null,
        showArchiveAction,
        !showArchiveAction
    )
}

