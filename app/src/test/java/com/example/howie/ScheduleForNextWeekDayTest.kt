package com.example.howie

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

class ScheduleForNextWeekDayTest {
    @Test
    fun `schedule for next specified day`() {
        val date = LocalDate.of(2021, Month.JANUARY, 3)
        assertEquals(DayOfWeek.SUNDAY, date.dayOfWeek)
        assertEquals(
            LocalDate.of(2021, Month.JANUARY, 4),
            ScheduleForNextWeekDay(DayOfWeek.MONDAY).scheduleNext(date)
        )
        assertEquals(
            LocalDate.of(2021, Month.JANUARY, 5),
            ScheduleForNextWeekDay(DayOfWeek.TUESDAY).scheduleNext(date)
        )
        assertEquals(
            LocalDate.of(2021, Month.JANUARY, 6),
            ScheduleForNextWeekDay(DayOfWeek.WEDNESDAY).scheduleNext(date)
        )
        assertEquals(
            LocalDate.of(2021, Month.JANUARY, 7),
            ScheduleForNextWeekDay(DayOfWeek.THURSDAY).scheduleNext(date)
        )
        assertEquals(
            LocalDate.of(2021, Month.JANUARY, 8),
            ScheduleForNextWeekDay(DayOfWeek.FRIDAY).scheduleNext(date)
        )
        assertEquals(
            LocalDate.of(2021, Month.JANUARY, 9),
            ScheduleForNextWeekDay(DayOfWeek.SATURDAY).scheduleNext(date)
        )
        assertEquals(
            LocalDate.of(2021, Month.JANUARY, 10),
            ScheduleForNextWeekDay(DayOfWeek.SUNDAY).scheduleNext(date)
        )
    }
}