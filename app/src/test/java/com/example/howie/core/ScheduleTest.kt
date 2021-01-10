package com.example.howie.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

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
}