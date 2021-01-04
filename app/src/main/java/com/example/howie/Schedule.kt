package com.example.howie

import java.time.LocalDate

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

fun Schedule.scheduleNext(date: LocalDate): LocalDate {
    return scheduleInXTimeUnits?.scheduleNext(date)
        ?: scheduleForNextWeekDay?.scheduleNext(date)
        ?: scheduleForScheduleForNextDayOfMonth!!.scheduleNext(date)
}