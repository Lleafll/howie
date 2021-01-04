package com.example.howie

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
}

fun ScheduleInXTimeUnits.scheduleNext(date: LocalDate): LocalDate {
    return when (timeUnit) {
        TimeUnit.DAY -> date.plusDays(quantity)
        TimeUnit.WEEK -> date.plusWeeks(quantity)
        TimeUnit.MONTH -> date.plusMonths(quantity)
        TimeUnit.YEAR -> date.plusYears(quantity)
    }
}