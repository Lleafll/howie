package com.example.howie

enum class TimeUnit {
    DAY, WEEK, MONTH, YEAR
}

enum class WeekDay {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

data class InXTimeUnits(val quantity: Int, val timeUnit: TimeUnit)

data class NextWeekDay(val weekDay: WeekDay)

data class NextDayOfMonth(val dayOfMonth: Int) {
    init {
        if (BuildConfig.DEBUG && !((dayOfMonth >= 1) && (dayOfMonth <= 31))) {
            error("Assertion failed")
        }
    }
}

class Schedule {
    // kotlin/Java do not support unions, so we set one of the members to an actual object and the others to null
    val inXTimeUnits: InXTimeUnits?
    val nextWeekDay: NextWeekDay?
    val nextDayOfMonth: NextDayOfMonth?

    constructor(inXTimeUnits: InXTimeUnits) {
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
