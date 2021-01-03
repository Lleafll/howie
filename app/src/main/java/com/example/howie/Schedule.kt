package com.example.howie

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

enum class TimeUnit {
    DAY, WEEK, MONTH, YEAR
}

data class ScheduleInXTimeUnits(val quantity: Long, val timeUnit: TimeUnit) {
    init {
        if (quantity < 1) {
            error("quantity ($quantity) can not by less than 1")
        }
    }
}

fun ScheduleInXTimeUnits.scheduleNext(date: LocalDate): LocalDate {
    return when (timeUnit) {
        TimeUnit.DAY -> date.plusDays(quantity)
        TimeUnit.WEEK -> date.plusWeeks(quantity)
        TimeUnit.MONTH -> date.plusMonths(quantity)
        TimeUnit.YEAR -> date.plusYears(quantity)
    }
}

data class ScheduleForNextWeekDay(val weekDay: DayOfWeek)

fun ScheduleForNextWeekDay.scheduleNext(date: LocalDate): LocalDate {
    return date.with(TemporalAdjusters.next(weekDay))
}

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

class Schedule {
    // kotlin/Java do not support unions, so we set one of the members to an actual object and the others to null
    val scheduleInXTimeUnits: ScheduleInXTimeUnits?
    val scheduleForNextWeekDay: ScheduleForNextWeekDay?
    val scheduleForScheduleForNextDayOfMonth: ScheduleForNextDayOfMonth?

    constructor(inXTimeUnits: ScheduleInXTimeUnits) {
        this.scheduleInXTimeUnits = inXTimeUnits
        scheduleForNextWeekDay = null
        scheduleForScheduleForNextDayOfMonth = null
    }

    constructor(scheduleForNextWeekDay: ScheduleForNextWeekDay) {
        scheduleInXTimeUnits = null
        this.scheduleForNextWeekDay = scheduleForNextWeekDay
        scheduleForScheduleForNextDayOfMonth = null
    }

    constructor(scheduleForNextDayOfMonth: ScheduleForNextDayOfMonth) {
        scheduleInXTimeUnits = null
        scheduleForNextWeekDay = null
        this.scheduleForScheduleForNextDayOfMonth = scheduleForNextDayOfMonth
    }
}