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

data class NextWeekDay(val weekDay: WeekDay)

data class NextDayOfMonth(val dayOfMonth: Int) {
    init {
        if (!((dayOfMonth >= 1) && (dayOfMonth <= 31))) {
            error("dayOfMonth ($dayOfMonth) not >= 1 and <= 31")
        }
    }
}

class Schedule {
    // kotlin/Java do not support unions, so we set one of the members to an actual object and the others to null
    val inXTimeUnits: ScheduleInXTimeUnits?
    val nextWeekDay: NextWeekDay?
    val nextDayOfMonth: NextDayOfMonth?

    constructor(inXTimeUnits: ScheduleInXTimeUnits) {
        this.inXTimeUnits = inXTimeUnits
        nextWeekDay = null
        nextDayOfMonth = null
    }

    constructor(nextWeekDay: NextWeekDay) {
        inXTimeUnits = null
        this.nextWeekDay = nextWeekDay
        nextDayOfMonth = null
    }

    constructor(nextDayOfMonth: NextDayOfMonth) {
        inXTimeUnits = null
        nextWeekDay = null
        this.nextDayOfMonth = nextDayOfMonth
    }
}