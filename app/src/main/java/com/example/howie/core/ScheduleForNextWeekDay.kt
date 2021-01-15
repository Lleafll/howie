package com.example.howie.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Parcelize
data class ScheduleForNextWeekDay(val weekDay: DayOfWeek) : Parcelable {
    override fun toString(): String {
        return when (weekDay) {
            DayOfWeek.MONDAY -> "Monday"
            DayOfWeek.TUESDAY -> "Tuesday"
            DayOfWeek.WEDNESDAY -> "Wednesday"
            DayOfWeek.THURSDAY -> "Thursday"
            DayOfWeek.FRIDAY -> "Friday"
            DayOfWeek.SATURDAY -> "Saturday"
            DayOfWeek.SUNDAY -> "Sunday"
        }
    }
}

fun ScheduleForNextWeekDay.scheduleNext(date: LocalDate): LocalDate {
    return date.with(TemporalAdjusters.next(weekDay))
}