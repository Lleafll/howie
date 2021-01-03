package com.example.howie

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScheduleTest {
    @Test
    fun `construct from InXTimeUnits`() {
        val schedule = Schedule(ScheduleInXTimeUnits(5, TimeUnit.DAY))
        assertEquals(ScheduleInXTimeUnits(5, TimeUnit.DAY), schedule.scheduleInXTimeUnits)
        assertNull(schedule.scheduleForNextWeekDay)
        assertNull(schedule.scheduleForScheduleForNextDayOfMonth)
    }

    @Test
    fun `construct from NextWeekDay`() {
        val schedule = Schedule(ScheduleForNextWeekDay(WeekDay.SATURDAY))
        assertNull(schedule.scheduleInXTimeUnits)
        assertEquals(ScheduleForNextWeekDay(WeekDay.SATURDAY), schedule.scheduleForNextWeekDay)
        assertNull(schedule.scheduleForScheduleForNextDayOfMonth)
    }

    @Test
    fun `construct from NextDayOfMonth`() {
        val schedule = Schedule(ScheduleForNextDayOfMonth(10))
        assertNull(schedule.scheduleInXTimeUnits)
        assertNull(schedule.scheduleForNextWeekDay)
        assertEquals(ScheduleForNextDayOfMonth(10), schedule.scheduleForScheduleForNextDayOfMonth)
    }
}