package com.example.howie.core

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek

class ScheduleForNextWeekDayTest {
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