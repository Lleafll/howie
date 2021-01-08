package com.example.howie.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainModelTest {
    @Test
    fun `getTaskListInformation return empty list when tasks are empty`() {
        val model = DomainModel(listOf())
        assertTrue(model.getTaskListInformation().isEmpty())
    }

    @Test
    fun `getTaskListInformation returns proper information on nonempty list`() {
        val model = DomainModel(listOf(TaskList("ABC", listOf())))
        assertEquals(
            listOf(TaskListInformation("ABC", TaskCounts(0, 0, 0, 0))),
            model.getTaskListInformation()
        )
    }

    @Test
    fun `getTaskListInformation returns proper information on nonempty list with task`() {
        val model = DomainModel(listOf(TaskList("ABC", listOf(Task("DEF")))))
        assertEquals(
            listOf(TaskListInformation("ABC", TaskCounts(0, 1, 0, 0))),
            model.getTaskListInformation()
        )
    }
}
