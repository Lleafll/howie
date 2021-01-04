package com.example.howie

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.Month

class ScheduleInXTimeUnitsTest {
    @Test(expected = IllegalStateException::class)
    fun `construct from smaller than 0 should throw`() {
        ScheduleInXTimeUnits(-1, TimeUnit.DAY)
    }

    @Test(expected = IllegalStateException::class)
    fun `construct from smaller than 1 should throw`() {
        ScheduleInXTimeUnits(0, TimeUnit.DAY)
    }

    @Test
    fun `construct from 1 should not throw`() {
        ScheduleInXTimeUnits(1, TimeUnit.DAY)
    }

    @Test
    fun `scheduleNext in 1 day`() {
        val schedule = ScheduleInXTimeUnits(1, TimeUnit.DAY)
        assertEquals(
            LocalDate.of(1980, Month.JANUARY, 2),
            schedule.scheduleNext(LocalDate.of(1980, Month.JANUARY, 1))
        )
        assertEquals(
            LocalDate.of(1981, Month.JANUARY, 1),
            schedule.scheduleNext(LocalDate.of(1980, Month.DECEMBER, 31))
        )
    }

    @Test
    fun `scheduleNext in 10 days`() {
        val schedule = ScheduleInXTimeUnits(10, TimeUnit.DAY)
        assertEquals(
            LocalDate.of(1980, Month.JANUARY, 11),
            schedule.scheduleNext(LocalDate.of(1980, Month.JANUARY, 1))
        )
        assertEquals(
            LocalDate.of(1981, Month.JANUARY, 10),
            schedule.scheduleNext(LocalDate.of(1980, Month.DECEMBER, 31))
        )
    }

    @Test
    fun `scheduleNext with leap day`() {
        val schedule = ScheduleInXTimeUnits(1, TimeUnit.DAY)
        assertEquals(
            LocalDate.of(2020, Month.FEBRUARY, 29),
            schedule.scheduleNext(LocalDate.of(2020, Month.FEBRUARY, 28))
        )
    }

    @Test
    fun `scheduleNext in 1 week`() {
        val schedule = ScheduleInXTimeUnits(1, TimeUnit.WEEK)
        assertEquals(
            LocalDate.of(1980, Month.JANUARY, 8),
            schedule.scheduleNext(LocalDate.of(1980, Month.JANUARY, 1))
        )
    }

    @Test
    fun `scheduleNext in 2 weeks`() {
        val schedule = ScheduleInXTimeUnits(2, TimeUnit.WEEK)
        assertEquals(
            LocalDate.of(1980, Month.JANUARY, 15),
            schedule.scheduleNext(LocalDate.of(1980, Month.JANUARY, 1))
        )
    }

    @Test
    fun `scheduleNext in 1 month`() {
        val schedule = ScheduleInXTimeUnits(1, TimeUnit.MONTH)
        assertEquals(
            LocalDate.of(1980, Month.FEBRUARY, 1),
            schedule.scheduleNext(LocalDate.of(1980, Month.JANUARY, 1))
        )
    }

    @Test
    fun `scheduleNext in 12 months`() {
        val schedule = ScheduleInXTimeUnits(12, TimeUnit.MONTH)
        assertEquals(
            LocalDate.of(1981, Month.JANUARY, 1),
            schedule.scheduleNext(LocalDate.of(1980, Month.JANUARY, 1))
        )
    }

    @Test
    fun `scheduleNext in 1 year`() {
        val schedule = ScheduleInXTimeUnits(1, TimeUnit.YEAR)
        assertEquals(
            LocalDate.of(1981, Month.JANUARY, 1),
            schedule.scheduleNext(LocalDate.of(1980, Month.JANUARY, 1))
        )
    }

    @Test
    fun `scheduleNext in 10 years`() {
        val schedule = ScheduleInXTimeUnits(10, TimeUnit.YEAR)
        assertEquals(
            LocalDate.of(1990, Month.JANUARY, 1),
            schedule.scheduleNext(LocalDate.of(1980, Month.JANUARY, 1))
        )
    }
}