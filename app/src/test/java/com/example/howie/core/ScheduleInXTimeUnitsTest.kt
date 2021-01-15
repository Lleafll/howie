package com.example.howie.core

import org.junit.Assert.assertEquals
import org.junit.Test

class ScheduleInXTimeUnitsTest {
    @Test
    fun `toString for all time units`() {
        assertEquals("1 Day", ScheduleInXTimeUnits(1, TimeUnit.DAY).toString())
        assertEquals("2 Days", ScheduleInXTimeUnits(2, TimeUnit.DAY).toString())
        assertEquals("1 Week", ScheduleInXTimeUnits(1, TimeUnit.WEEK).toString())
        assertEquals("3 Weeks", ScheduleInXTimeUnits(3, TimeUnit.WEEK).toString())
        assertEquals("1 Month", ScheduleInXTimeUnits(1, TimeUnit.MONTH).toString())
        assertEquals("4 Months", ScheduleInXTimeUnits(4, TimeUnit.MONTH).toString())
        assertEquals("1 Year", ScheduleInXTimeUnits(1, TimeUnit.YEAR).toString())
        assertEquals("5 Years", ScheduleInXTimeUnits(5, TimeUnit.YEAR).toString())
    }
}