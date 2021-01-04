package com.example.howie

import android.os.Parcelable
import androidx.room.Embedded
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
class Schedule(
    // kotlin/Java do not support unions, so we set one of the members to an actual object and the others to null
    @Embedded var scheduleInXTimeUnits: ScheduleInXTimeUnits?,
    @Embedded var scheduleForNextWeekDay: ScheduleForNextWeekDay?,
    @Embedded var scheduleForScheduleForNextDayOfMonth: ScheduleForNextDayOfMonth?
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
}

fun Schedule.scheduleNext(date: LocalDate): LocalDate {
    return scheduleInXTimeUnits?.scheduleNext(date)
        ?: scheduleForNextWeekDay?.scheduleNext(date)
        ?: scheduleForScheduleForNextDayOfMonth!!.scheduleNext(date)
}