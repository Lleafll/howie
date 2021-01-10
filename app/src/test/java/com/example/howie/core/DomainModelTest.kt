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
                    IndexedTask(0, Task("A", snoozed = null)),
                    IndexedTask(1, Task("B", snoozed = null))
                ),
                listOf(
                    IndexedTask(2, Task("C", snoozed = date)),
                    IndexedTask(3, Task("D", snoozed = date)),
                    IndexedTask(4, Task("E", snoozed = date))
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

    @Test(expected = IndexOutOfBoundsException::class)
    fun `getTaskListName throws on invalid index`() {
        val model = DomainModel(listOf())
        model.getTaskListName(123)
    }

    @Test
    fun `getTaskListName returns proper names`() {
        val model = DomainModel(
            listOf(
                TaskList("ABC", listOf()),
                TaskList("DEF", listOf()),
                TaskList("GHI", listOf())
            )
        )
        assertEquals("ABC", model.getTaskListName(0))
        assertEquals("DEF", model.getTaskListName(1))
        assertEquals("GHI", model.getTaskListName(2))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `getTaskCounts throws when passed invalid index`() {
        val model = DomainModel(listOf())
        model.getTaskCounts(123)
    }

    @Test
    fun `getTaskCounts pretty much empty on empty task list`() {
        val model = DomainModel(listOf(TaskList("", listOf())))
        assertEquals(TaskCounts(0, 0, 0, 0), model.getTaskCounts(0))
    }

    @Test
    fun `getTaskCounts does not count archived tasks`() {
        val archived = LocalDate.MIN
        val model = DomainModel(listOf(TaskList("", listOf(Task("", archived = archived)))))
        assertEquals(TaskCounts(0, 0, 0, 0), model.getTaskCounts(0))
    }

    @Test
    fun `getTaskCounts does not count snoozed tasks which are in the future`() {
        val snoozed = LocalDate.MAX
        val model = DomainModel(listOf(TaskList("", listOf(Task("", snoozed = snoozed)))))
        assertEquals(TaskCounts(0, 0, 0, 0), model.getTaskCounts(0))
    }

    @Test
    fun `getTaskCounts returns proper counts`() {
        val model = DomainModel(
            listOf(
                TaskList(
                    "TaskList1",
                    listOf(
                        // 1 Do task which is not snoozed
                        Task("Do1", due = LocalDate.MIN, snoozed = LocalDate.MAX), // snoozed
                        Task("Do2", due = LocalDate.MIN, snoozed = LocalDate.MIN), // not snoozed
                        // 2 Decide tasks
                        Task("Decide1"),
                        Task("Decide2"),
                        // 3 Delegate tasks
                        Task("Delegate1", due = LocalDate.MIN, importance = Importance.UNIMPORTANT),
                        Task("Delegate2", due = LocalDate.MIN, importance = Importance.UNIMPORTANT),
                        Task("Delegate3", due = LocalDate.MIN, importance = Importance.UNIMPORTANT),
                        // 0 Drop tasks
                        Task(
                            "Drop1",
                            importance = Importance.UNIMPORTANT,
                            archived = LocalDate.now()
                        )
                    )
                ),
                TaskList(
                    "TaskList2",
                    listOf(
                        // Random tasks
                        Task("2Decide1"),
                        Task("2Decide2"),
                        Task(
                            "2Delegate1",
                            due = LocalDate.MIN,
                            importance = Importance.UNIMPORTANT
                        ),
                        Task(
                            "2Delegate2",
                            due = LocalDate.MIN,
                            importance = Importance.UNIMPORTANT
                        ),
                        Task(
                            "2Delegate3",
                            due = LocalDate.MIN,
                            importance = Importance.UNIMPORTANT
                        ),
                    )
                )
            )
        )
        assertEquals(TaskCounts(1, 2, 3, 0), model.getTaskCounts(0))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `getTask throws for invalid taskList index`() {
        val model = DomainModel(listOf())
        model.getTask(123, 0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `getTask throws for invalid task index`() {
        val model = DomainModel(listOf())
        model.getTask(0, 123)
    }

    @Test
    fun `getTask works properly for valid indexes`() {
        val model = DomainModel(
            listOf(
                TaskList(
                    "", listOf(
                        Task("1"),
                        Task("2")
                    )
                ),
                TaskList(
                    "", listOf(
                        Task("3")
                    )
                )
            )
        )
        assertEquals(Task("1"), model.getTask(0, 0))
        assertEquals(Task("2"), model.getTask(0, 1))
        assertEquals(Task("3"), model.getTask(1, 0))
    }
}
