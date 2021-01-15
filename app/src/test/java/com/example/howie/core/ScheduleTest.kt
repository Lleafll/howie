package com.example.howie.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.time.DayOfWeek

class ScheduleTest {
    @Test
    fun `Schedule comparison`() {
        assertEquals(
            Schedule(ScheduleInXTimeUnits(1, TimeUnit.WEEK)),
            Schedule(ScheduleInXTimeUnits(1, TimeUnit.WEEK))
        )
        assertNotEquals(
            Schedule(ScheduleInXTimeUnits(1, TimeUnit.WEEK)),
            Schedule(ScheduleInXTimeUnits(2, TimeUnit.WEEK))
        )
        assertNotEquals(Schedule(ScheduleInXTimeUnits(1, TimeUnit.WEEK)), null)
        assertNotEquals(Schedule(ScheduleInXTimeUnits(1, TimeUnit.WEEK)), 1)
    }

    @Test
    fun `toString for all types`() {
        assertEquals("1 Day", Schedule(ScheduleInXTimeUnits(1, TimeUnit.DAY)).toString())
        assertEquals("30.", Schedule(ScheduleForNextDayOfMonth(30)).toString())
        assertEquals("Sunday", Schedule(ScheduleForNextWeekDay(DayOfWeek.SUNDAY)).toString())
    }
}