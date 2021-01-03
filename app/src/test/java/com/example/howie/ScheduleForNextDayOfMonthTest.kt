package com.example.howie

import org.junit.Test

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

    @Test()
    fun `construction does not throw for 1 to 31`() {
        for (i in 1..31) {
            ScheduleForNextDayOfMonth(i)
        }
    }
}