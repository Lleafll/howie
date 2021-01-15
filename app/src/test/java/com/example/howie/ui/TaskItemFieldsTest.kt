package com.example.howie.ui

import com.example.howie.core.IndexedTask
import com.example.howie.core.Task
import com.example.howie.core.TaskIndex
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskItemFieldsTest {
    @Test
    fun `IndexedTask_toTaskItemFields for default IndexedTask`() {
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
}