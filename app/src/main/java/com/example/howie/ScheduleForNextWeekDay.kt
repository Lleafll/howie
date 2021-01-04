package com.example.howie

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Parcelize
data class ScheduleForNextWeekDay(val weekDay: DayOfWeek) : Parcelable

fun ScheduleForNextWeekDay.scheduleNext(date: LocalDate): LocalDate {
    return date.with(TemporalAdjusters.next(weekDay))
}