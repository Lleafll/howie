package com.example.howie

import java.time.LocalDate

enum class TimeUnit {
    DAY, WEEK, MONTH, YEAR
}

enum class WeekDay {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
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

data class ScheduleForNextWeekDay(val weekDay: WeekDay)

fun ScheduleForNextWeekDay.scheduleNext(date: LocalDate): LocalDate {
    return LocalDate.ofEpochDay(0)
}

data class ScheduleForNextDayOfMonth(val dayOfMonth: Int) {
    init {
        if (!((dayOfMonth >= 1) && (dayOfMonth <= 31))) {
            error("dayOfMonth ($dayOfMonth) not >= 1 and <= 31")
        }
    }
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