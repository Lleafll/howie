package com.example.howie.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

enum class TimeUnit {
    DAY, WEEK, MONTH, YEAR
}

@Parcelize
data class ScheduleInXTimeUnits(val quantity: Long, val timeUnit: TimeUnit) : Parcelable {
    init {
        if (quantity < 1) {
            error("quantity ($quantity) can not by less than 1")
        }
    }

    override fun toString(): String {
        return "$quantity ${timeUnit.toString(quantity != 1L)}"
    }
}

private fun TimeUnit.toString(plural: Boolean): String {
    var string = when (this) {
        TimeUnit.DAY -> "Day"
        TimeUnit.WEEK -> "Week"
        TimeUnit.MONTH -> "Month"
        TimeUnit.YEAR -> "Years"
    }
    if (plural) {
        string += "s"
    }
    return string
}

fun ScheduleInXTimeUnits.scheduleNext(date: LocalDate): LocalDate {
    return when (timeUnit) {
        TimeUnit.DAY -> date.plusDays(quantity)
        TimeUnit.WEEK -> date.plusWeeks(quantity)
        TimeUnit.MONTH -> date.plusMonths(quantity)
        TimeUnit.YEAR -> date.plusYears(quantity)
    }
}