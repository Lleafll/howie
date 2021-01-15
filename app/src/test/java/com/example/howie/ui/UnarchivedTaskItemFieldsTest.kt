package com.example.howie.ui

import com.example.howie.core.*
import org.junit.Assert.assertEquals
import org.junit.Test

class UnarchivedTaskItemFieldsTest {
    @Test
    fun `toUnarchivedTaskItemFields for empty UnarchivedTasks`() {
        assertEquals(
            UnarchivedTaskItemFields(emptyList(), emptyList()),
            UnarchivedTasks(emptyList(), emptyList()).toUnarchivedTaskItemFields()
        )
    }

    @Test
    fun toUnarchivedTaskItemFields() {
        assertEquals(
            UnarchivedTaskItemFields(
                listOf(
                    TaskItemFields(
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
                ),
                listOf(
                    TaskItemFields(
                        TaskIndex(1),
                        "DEF",
                        due = null,
                        snoozed = null,
                        archived = null,
                        snoozedToTomorrow = true,
                        removeSnoozed = false,
                        reschedule = null,
                        archive = true,
                        unarchive = false
                    )
                )
            ),
            UnarchivedTasks(
                listOf(
                    IndexedTask(
                        TaskIndex(0),
                        Task(
                            "ABC",
                            importance = Importance.IMPORTANT,
                            due = null,
                            snoozed = null,
                            schedule = null,
                            archived = null
                        )
                    )
                ),
                listOf(
                    IndexedTask(
                        TaskIndex(1),
                        Task(
                            "DEF",
                            importance = Importance.IMPORTANT,
                            due = null,
                            snoozed = null,
                            schedule = null,
                            archived = null
                        )
                    )
                )
            ).toUnarchivedTaskItemFields()
        )
    }
}