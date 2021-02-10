package com.lorenz.howie

import com.lorenz.howie.core.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class TaskTest {
    @Test
    fun isSnoozed() {
        assertFalse(Task("", snoozed = null).isSnoozed())
        assertFalse(Task("", snoozed = LocalDate.MIN).isSnoozed())
        assertTrue(Task("", snoozed = LocalDate.MAX).isSnoozed())
        assertFalse(Task("", snoozed = LocalDate.now()).isSnoozed())
        assertTrue(Task("", snoozed = LocalDate.now().plusDays(1)).isSnoozed())
    }

    @Test
    fun isArchived() {
        assertTrue(Task("", archived = LocalDate.MAX).isArchived())
        assertTrue(Task("", archived = LocalDate.MIN).isArchived())
        assertTrue(Task("", archived = LocalDate.of(1234, 12, 12)).isArchived())
        assertFalse(Task("").isArchived())
        assertFalse(Task("", archived = null).isArchived())
    }

    @Test
    fun `scheduleNext with null schedule`() {
        assertNull(Task("").scheduleNext())
    }

    @Test
    fun `scheduleNext when both due and snoozed are set`() {
        val tomorrow = LocalDate.now().plusDays(1)
        val scheduleToTomorrow = Schedule(ScheduleInXTimeUnits(1, TimeUnit.DAY))
        assertEquals(
            Task("", due = tomorrow, snoozed = tomorrow, schedule = scheduleToTomorrow),
            Task(
                "",
                due = LocalDate.MIN,
                snoozed = LocalDate.MIN,
                schedule = scheduleToTomorrow
            ).scheduleNext()
        )
    }

    @Test
    fun `scheduleNext when neither due nor snoozed are set`() {
        val tomorrow = LocalDate.now().plusDays(1)
        val scheduleToTomorrow = Schedule(ScheduleInXTimeUnits(1, TimeUnit.DAY))
        assertEquals(
            Task("", due = null, snoozed = tomorrow, schedule = scheduleToTomorrow),
            Task("", schedule = scheduleToTomorrow).scheduleNext()
        )
    }
}