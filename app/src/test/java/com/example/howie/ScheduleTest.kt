package com.example.howie

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScheduleTest {
    @Test
    fun `construct from InXTimeUnits`() {
        val schedule = Schedule(ScheduleInXTimeUnits(5, TimeUnit.DAY))
        assertEquals(ScheduleInXTimeUnits(5, TimeUnit.DAY), schedule.inXTimeUnits)
        assertNull(schedule.nextWeekDay)
        assertNull(schedule.nextDayOfMonth)
    }

    @Test
    fun `construct from NextWeekDay`() {
        val schedule = Schedule(NextWeekDay(WeekDay.SATURDAY))
        assertNull(schedule.inXTimeUnits)
        assertEquals(NextWeekDay(WeekDay.SATURDAY), schedule.nextWeekDay)
        assertNull(schedule.nextDayOfMonth)
    }

    @Test
    fun `construct from NextDayOfMonth`() {
        val schedule = Schedule(NextDayOfMonth(10))
        assertNull(schedule.inXTimeUnits)
        assertNull(schedule.nextWeekDay)
        assertEquals(NextDayOfMonth(10), schedule.nextDayOfMonth)
    }
}