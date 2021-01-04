package com.example.howie

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class ScheduleForNextWeekDay(val weekDay: DayOfWeek)

fun ScheduleForNextWeekDay.scheduleNext(date: LocalDate): LocalDate {
    return date.with(TemporalAdjusters.next(weekDay))
}