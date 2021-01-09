package com.example.howie.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

enum class Importance {
    IMPORTANT, UNIMPORTANT
}

@Parcelize
data class Task(
    val name: String,
    val importance: Importance = Importance.IMPORTANT,
    val due: LocalDate? = null,
    val snoozed: LocalDate? = null,
    val schedule: Schedule? = null,
    val archived: LocalDate? = null
) : Parcelable

enum class TaskCategory {
    DO, DECIDE, DELEGATE, DROP
}

fun Task.category(): TaskCategory = if (importance == Importance.IMPORTANT) {
    if (due != null) {
        TaskCategory.DO
    } else {
        TaskCategory.DECIDE
    }
} else {
    if (due != null) {
        TaskCategory.DELEGATE
    } else {
        TaskCategory.DROP
    }
}

fun Task.scheduleNext(): Task? = if (schedule == null) {
    null
} else {
    val newDate = schedule.scheduleNext(LocalDate.now())
    copy(due = newDate, snoozed = newDate)
}

fun Task.isSnoozed(): Boolean {
    return if (snoozed == null) {
        false
    } else {
        snoozed > LocalDate.now()
    }
}
