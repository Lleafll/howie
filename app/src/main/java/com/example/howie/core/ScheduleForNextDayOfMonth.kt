package com.example.howie.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
data class ScheduleForNextDayOfMonth(val dayOfMonth: Int) : Parcelable {
    init {
        if (!((dayOfMonth >= 1) && (dayOfMonth <= 31))) {
            error("dayOfMonth ($dayOfMonth) not >= 1 and <= 31")
        }
    }

    override fun toString(): String {
        return "$dayOfMonth."
    }
}

fun ScheduleForNextDayOfMonth.scheduleNext(date: LocalDate): LocalDate {
    val adjustedDate = if (date.dayOfMonth >= dayOfMonth) date.plusMonths(1) else date
    return adjustedDate.withDayOfMonth(dayOfMonth.coerceAtMost(adjustedDate.lengthOfMonth()))
}