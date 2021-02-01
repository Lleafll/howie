package com.lorenz.howie.ui

import com.lorenz.howie.core.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class TaskItemFieldsTest {
    @Test
    fun `toTaskItemFields for default IndexedTask`() {
        val task = IndexedTask(
            TaskIndex(0),
            Task("ABC")
        )
        val expected = TaskItemFields(
            TaskIndex(0),
            "ABC",
            due = null,
            snoozed = null,
            archived = null,
            snoozedToTomorrow = true,
            removeSnoozed = false,
            reschedule = null,
            archive = true,
            unarchive = false
        )
        assertEquals(expected, task.toTaskItemFields())
    }

    @Test
    fun `toTaskItemFields for archived IndexedTask`() {
        val task = IndexedTask(
            TaskIndex(0),
            Task("ABC", archived = LocalDate.of(1111, 11, 11)),
        )
        val expected = TaskItemFields(
            TaskIndex(0),
            "ABC",
            due = null,
            snoozed = null,
            archived = "1111-11-11",
            snoozedToTomorrow = false,
            removeSnoozed = false,
            reschedule = null,
            archive = false,
            unarchive = true
        )
        assertEquals(expected, task.toTaskItemFields())
    }

    @Test
    fun `toTaskItemFields for unarchived IndexedTask with snooze in the past`() {
        val task = IndexedTask(
            TaskIndex(0),
            Task("ABC", snoozed = LocalDate.of(1111, 11, 11)),
        )
        val expected = TaskItemFields(
            TaskIndex(0),
            "ABC",
            due = null,
            snoozed = null,
            archived = null,
            snoozedToTomorrow = true,
            removeSnoozed = false,
            reschedule = null,
            archive = true,
            unarchive = false
        )
        assertEquals(expected, task.toTaskItemFields())
    }

    @Test
    fun `toTaskItemFields for unarchived IndexedTask with snooze in the future and schedule`() {
        val task = IndexedTask(
            TaskIndex(0),
            Task(
                "ABC",
                snoozed = LocalDate.of(2222, 11, 11),
                schedule = Schedule(ScheduleInXTimeUnits(1, TimeUnit.DAY))
            ),
        )
        val expected = TaskItemFields(
            TaskIndex(0),
            "ABC",
            due = null,
            snoozed = "2222-11-11",
            archived = null,
            snoozedToTomorrow = false,
            removeSnoozed = true,
            reschedule = "1 Day",
            archive = true,
            unarchive = false
        )
        assertEquals(expected, task.toTaskItemFields())
    }
}