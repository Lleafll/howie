package com.lorenz.howie

import com.lorenz.howie.core.ScheduleForNextWeekDay
import com.lorenz.howie.core.scheduleNext
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

    @Test
    fun `toString tests`() {
        assertEquals("Monday", ScheduleForNextWeekDay(DayOfWeek.MONDAY).toString())
        assertEquals("Tuesday", ScheduleForNextWeekDay(DayOfWeek.TUESDAY).toString())
        assertEquals("Wednesday", ScheduleForNextWeekDay(DayOfWeek.WEDNESDAY).toString())
        assertEquals("Thursday", ScheduleForNextWeekDay(DayOfWeek.THURSDAY).toString())
        assertEquals("Friday", ScheduleForNextWeekDay(DayOfWeek.FRIDAY).toString())
        assertEquals("Saturday", ScheduleForNextWeekDay(DayOfWeek.SATURDAY).toString())
        assertEquals("Sunday", ScheduleForNextWeekDay(DayOfWeek.SUNDAY).toString())
    }
}