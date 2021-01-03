package com.example.howie

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScheduleTest {
    @Test(expected = IllegalStateException::class)
    fun `nextDayOfMonth construction throws for 0`() {
        NextDayOfMonth(0)
    }

    @Test(expected = IllegalStateException::class)
    fun `nextDayOfMonth construction throws for -1`() {
        NextDayOfMonth(-1)
    }

    @Test(expected = IllegalStateException::class)
    fun `nextDayOfMonth construction throws for 32`() {
        NextDayOfMonth(32)
    }

    @Test()
    fun `nextDayOfMonth construction does not throw for 1 to 31`() {
        for (i in 1..31) {
            NextDayOfMonth(i)
        }
    }

    @Test
    fun `construct from InXTimeUnits`() {
        val inXTimeUnits = InXTimeUnits(5, TimeUnit.DAY)
        val schedule = Schedule(inXTimeUnits)
        assertEquals(schedule.inXTimeUnits, inXTimeUnits)
        assertNull(schedule.nextWeekDay)
        assertNull(schedule.nextDayOfMonth)
    }

    @Test
    fun `construct from NextWeekDay`() {
        val nextWeekDay = NextWeekDay(WeekDay.SATURDAY)
        val schedule = Schedule(nextWeekDay)
        assertNull(schedule.inXTimeUnits)
        assertEquals(schedule.nextWeekDay, nextWeekDay)
        assertNull(schedule.nextDayOfMonth)
    }

    @Test
    fun `construct from NextDayOfMonth`() {
        val nextDayOfMonth = NextDayOfMonth(10)
        val schedule = Schedule(nextDayOfMonth)
        assertNull(schedule.inXTimeUnits)
        assertNull(schedule.nextWeekDay)
        assertEquals(schedule.nextDayOfMonth, nextDayOfMonth)
    }
}