package com.example.howie.core

import org.junit.Assert.assertEquals
import org.junit.Test

class ScheduleForNextDayOfMonthTest {
    @Test
    fun `toString tests`() {
        assertEquals("1.", ScheduleForNextDayOfMonth(1).toString())
        assertEquals("30.", ScheduleForNextDayOfMonth(30).toString())
    }
}