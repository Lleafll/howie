package com.example.howie

import com.example.howie.core.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

class ScheduleTest {
    @Test
    fun `construct from InXTimeUnits`() {
        val schedule = Schedule(ScheduleInXTimeUnits(5, TimeUnit.DAY))
        assertEquals(ScheduleInXTimeUnits(5, TimeUnit.DAY), schedule.scheduleInXTimeUnits)
        assertNull(schedule.scheduleForNextWeekDay)
        assertNull(schedule.scheduleForNextDayOfMonth)
    }

    @Test
    fun `construct from NextWeekDay`() {
        val schedule = Schedule(ScheduleForNextWeekDay(DayOfWeek.SATURDAY))
        assertNull(schedule.scheduleInXTimeUnits)
        assertEquals(ScheduleForNextWeekDay(DayOfWeek.SATURDAY), schedule.scheduleForNextWeekDay)
        assertNull(schedule.scheduleForNextDayOfMonth)
    }

    @Test
    fun `construct from NextDayOfMonth`() {
        val schedule = Schedule(ScheduleForNextDayOfMonth(10))
        assertNull(schedule.scheduleInXTimeUnits)
        assertNull(schedule.scheduleForNextWeekDay)
        assertEquals(ScheduleForNextDayOfMonth(10), schedule.scheduleForNextDayOfMonth)
    }

    @Test
    fun `scheduleNext in 1 day`() {
        assertEquals(
            LocalDate.of(2020, Month.JANUARY, 2),
            Schedule(ScheduleInXTimeUnits(1, TimeUnit.DAY)).scheduleNext(
                LocalDate.of(2020, Month.JANUARY, 1)
            )
        )
    }

    @Test
    fun `scheduleNext for next Monday`() {
        val date = LocalDate.of(2021, Month.JANUARY, 3)
        assertEquals(DayOfWeek.SUNDAY, date.dayOfWeek)
        assertEquals(
            LocalDate.of(2021, Month.JANUARY, 4),
            Schedule(ScheduleForNextWeekDay(DayOfWeek.MONDAY)).scheduleNext(date)
        )
    }

    @Test
    fun `scheduleNext for next 31st day of month`() {
        assertEquals(
            LocalDate.of(2020, Month.FEBRUARY, 29),
            Schedule(ScheduleForNextDayOfMonth(31)).scheduleNext(
                LocalDate.of(2020, Month.JANUARY, 31)
            )
        )
    }
}