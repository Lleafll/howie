package com.example.howie

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

class ScheduleForNextWeekDayTest {
    @Test
    fun `schedule for next MONDAY`() {
        val date = LocalDate.of(2021, Month.JANUARY, 3)
        assertEquals(DayOfWeek.SUNDAY, date.dayOfWeek)
        assertEquals(
            LocalDate.of(2021, Month.JANUARY, 4),
            ScheduleForNextWeekDay(DayOfWeek.MONDAY).scheduleNext(date)
        )
    }
}