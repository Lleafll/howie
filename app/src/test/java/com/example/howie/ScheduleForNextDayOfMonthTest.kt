package com.example.howie

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.Month

class ScheduleForNextDayOfMonthTest {
    @Test(expected = IllegalStateException::class)
    fun `construction throws for 0`() {
        ScheduleForNextDayOfMonth(0)
    }

    @Test(expected = IllegalStateException::class)
    fun `construction throws for -1`() {
        ScheduleForNextDayOfMonth(-1)
    }

    @Test(expected = IllegalStateException::class)
    fun `construction throws for 32`() {
        ScheduleForNextDayOfMonth(32)
    }

    @Test
    fun `construction does not throw for 1 to 31`() {
        for (i in 1..31) {
            ScheduleForNextDayOfMonth(i)
        }
    }

    @Test
    fun `scheduleNext to next first day of the month`() {
        val schedule = ScheduleForNextDayOfMonth(1)
        assertEquals(
            LocalDate.of(2020, Month.FEBRUARY, 1),
            schedule.scheduleNext(LocalDate.of(2020, Month.JANUARY, 1))
        )
    }

    @Test
    fun `scheduleNext to day which is in the current month`() {
        val schedule = ScheduleForNextDayOfMonth(5)
        assertEquals(
            LocalDate.of(2020, Month.JANUARY, 5),
            schedule.scheduleNext(LocalDate.of(2020, Month.JANUARY, 1))
        )
    }

    @Test
    fun `scheduleNext for day in next month which would be invalid`() {
        val schedule = ScheduleForNextDayOfMonth(31)
        assertEquals(
            LocalDate.of(2020, Month.FEBRUARY, 29),
            schedule.scheduleNext(LocalDate.of(2020, Month.JANUARY, 31))
        )
    }
}