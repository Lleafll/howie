package com.example.howie

import java.time.LocalDate

data class ScheduleForNextDayOfMonth(val dayOfMonth: Int) {
    init {
        if (!((dayOfMonth >= 1) && (dayOfMonth <= 31))) {
            error("dayOfMonth ($dayOfMonth) not >= 1 and <= 31")
        }
    }
}

fun ScheduleForNextDayOfMonth.scheduleNext(date: LocalDate): LocalDate {
    val adjustedDate = if (date.dayOfMonth >= dayOfMonth) date.plusMonths(1) else date
    return adjustedDate.withDayOfMonth(dayOfMonth.coerceAtMost(adjustedDate.lengthOfMonth()))
}