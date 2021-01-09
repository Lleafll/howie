package com.example.howie.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DomainModelTest {
    @Test
    fun `getTaskListInformation return empty information when tasks are empty`() {
        val model = DomainModel(listOf())
        assertTrue(model.getTaskListInformation().first().taskCounts == TaskCounts(0, 0, 0, 0))
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

    @Test(expected = IndexOutOfBoundsException::class)
    fun `getUnarchivedTasks throws IllegalArgumentException when passing invalid taskList`() {
        val model = DomainModel(listOf())
        model.getUnarchivedTasks(123, TaskCategory.DO)
    }

    @Test
    fun `getUnarchivedTasks returns nothing when task list is empty`() {
        val model = DomainModel(listOf(TaskList("Name", listOf())))
        assertEquals(
            UnarchivedTasks(listOf(), listOf()),
            model.getUnarchivedTasks(0, TaskCategory.DO)
        )
    }

    @Test
    fun `getUnarchivedTasks returns proper data`() {
        val date = LocalDate.now()
        val model = DomainModel(
            listOf(
                TaskList(
                    "Name1",
                    listOf(
                        Task("123", snoozed = null)
                    )
                ),
                TaskList(
                    "Name2",
                    listOf(
                        // 2 Unsnoozed tasks
                        Task("A", snoozed = null),
                        Task("B", snoozed = null),
                        // 3 Snoozed tasks
                        Task("C", snoozed = date),
                        Task("D", snoozed = date),
                        Task("E", snoozed = date),
                        // 1 Archived task
                        Task("F", archived = date),
                        // 1 Unimportant task
                        Task("G", Importance.UNIMPORTANT)
                    )
                )
            )
        )
        assertEquals(
            UnarchivedTasks(
                listOf(
                    Task("A", snoozed = null),
                    Task("B", snoozed = null)
                ),
                listOf(
                    Task("C", snoozed = date),
                    Task("D", snoozed = date),
                    Task("E", snoozed = date)
                )
            ),
            model.getUnarchivedTasks(1, TaskCategory.DECIDE)
        )
    }

    @Test
    fun `getTaskListName returns default name on model which got passed empty task lists`() {
        val model = DomainModel(listOf())
        assertEquals("Tasks", model.getTaskListName(0))
    }
}
