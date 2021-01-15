package com.example.howie.core

import android.os.Parcelable
import androidx.room.Embedded
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
class Schedule(
    // kotlin/Java do not support unions, so we set one of the members to an actual object and the others to null
    @Embedded(prefix = "inX") var scheduleInXTimeUnits: ScheduleInXTimeUnits?,
    @Embedded(prefix = "forNextWeekDay") var scheduleForNextWeekDay: ScheduleForNextWeekDay?,
    @Embedded(prefix = "forNextDayOfMonth") var scheduleForNextDayOfMonth: ScheduleForNextDayOfMonth?
) : Parcelable {

    constructor(inXTimeUnits: ScheduleInXTimeUnits) : this(inXTimeUnits, null, null)

    constructor(scheduleForNextWeekDay: ScheduleForNextWeekDay) : this(
        null,
        scheduleForNextWeekDay,
        null
    )

    constructor(scheduleForNextDayOfMonth: ScheduleForNextDayOfMonth) : this(
        null,
        null,
        scheduleForNextDayOfMonth
    )

    override fun equals(other: Any?): Boolean {
        return if (other is Schedule) {
            scheduleInXTimeUnits == other.scheduleInXTimeUnits &&
                    scheduleForNextWeekDay == other.scheduleForNextWeekDay &&
                    scheduleForNextDayOfMonth == other.scheduleForNextDayOfMonth
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = scheduleInXTimeUnits?.hashCode() ?: 0
        result = 31 * result + (scheduleForNextWeekDay?.hashCode() ?: 0)
        result = 31 * result + (scheduleForNextDayOfMonth?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return scheduleInXTimeUnits?.toString()
            ?: scheduleForNextWeekDay?.toString()
            ?: scheduleForNextDayOfMonth!!.toString()
    }
}

fun Schedule.scheduleNext(date: LocalDate): LocalDate {
    return scheduleInXTimeUnits?.scheduleNext(date)
        ?: scheduleForNextWeekDay?.scheduleNext(date)
        ?: scheduleForNextDayOfMonth!!.scheduleNext(date)
}