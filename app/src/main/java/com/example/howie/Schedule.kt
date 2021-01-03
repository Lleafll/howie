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
        if (BuildConfig.DEBUG && !((dayOfMonth > 0) && (dayOfMonth <= 31))) {
            error("Assertion failed")
        }
    }
}

class Schedule {
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
