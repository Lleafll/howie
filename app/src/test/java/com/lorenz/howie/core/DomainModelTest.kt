package com.lorenz.howie.core

import org.junit.Assert.*
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
        val model = DomainModel(listOf(TaskList("ABC", mutableListOf())))
        assertEquals(
            listOf(TaskListInformation("ABC", TaskCounts(0, 0, 0, 0))),
            model.getTaskListInformation()
        )
    }

    @Test
    fun `getTaskListInformation returns proper information on nonempty list with task`() {
        val model = DomainModel(listOf(TaskList("ABC", mutableListOf(Task("DEF")))))
        assertEquals(
            listOf(TaskListInformation("ABC", TaskCounts(0, 1, 0, 0))),
            model.getTaskListInformation()
        )
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `getUnarchivedTasks throws IllegalArgumentException when passing invalid taskList`() {
        val model = DomainModel(listOf())
        model.getUnarchivedTasks(TaskListIndex(123), TaskCategory.DO)
    }

    @Test
    fun `getUnarchivedTasks returns nothing when task list is empty`() {
        val model = DomainModel(listOf(TaskList("Name", mutableListOf())))
        assertEquals(
            UnarchivedTasks(listOf(), listOf()),
            model.getUnarchivedTasks(TaskListIndex(0), TaskCategory.DO)
        )
    }

    @Test
    fun `getUnarchivedTasks returns proper data`() {
        val date = LocalDate.MAX
        val model = DomainModel(
            listOf(
                TaskList(
                    "Name1",
                    mutableListOf(
                        Task("123", snoozed = null)
                    )
                ),
                TaskList(
                    "Name2",
                    mutableListOf(
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
                    IndexedTask(TaskIndex(0), Task("A", snoozed = null)),
                    IndexedTask(TaskIndex(1), Task("B", snoozed = null))
                ),
                listOf(
                    IndexedTask(TaskIndex(2), Task("C", snoozed = date)),
                    IndexedTask(TaskIndex(3), Task("D", snoozed = date)),
                    IndexedTask(TaskIndex(4), Task("E", snoozed = date))
                )
            ),
            model.getUnarchivedTasks(TaskListIndex(1), TaskCategory.DECIDE)
        )
    }

    @Test
    fun `getTaskListName returns default name on model which got passed empty task lists`() {
        val model = DomainModel(listOf())
        assertEquals("Tasks", model.getTaskListName(TaskListIndex(0)))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `getTaskListName throws on invalid index`() {
        val model = DomainModel(listOf())
        model.getTaskListName(TaskListIndex(123))
    }

    @Test
    fun `getTaskListName returns proper names`() {
        val model = DomainModel(
            listOf(
                TaskList("ABC", mutableListOf()),
                TaskList("DEF", mutableListOf()),
                TaskList("GHI", mutableListOf())
            )
        )
        assertEquals("ABC", model.getTaskListName(TaskListIndex(0)))
        assertEquals("DEF", model.getTaskListName(TaskListIndex(1)))
        assertEquals("GHI", model.getTaskListName(TaskListIndex(2)))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `getTaskCounts throws when passed invalid index`() {
        val model = DomainModel(listOf())
        model.getTaskCounts(TaskListIndex(123))
    }

    @Test
    fun `getTaskCounts pretty much empty on empty task list`() {
        val model = DomainModel(listOf(TaskList("", mutableListOf())))
        assertEquals(TaskCounts(0, 0, 0, 0), model.getTaskCounts(TaskListIndex(0)))
    }

    @Test
    fun `getTaskCounts does not count archived tasks`() {
        val archived = LocalDate.MIN
        val model = DomainModel(listOf(TaskList("", mutableListOf(Task("", archived = archived)))))
        assertEquals(TaskCounts(0, 0, 0, 0), model.getTaskCounts(TaskListIndex(0)))
    }

    @Test
    fun `getTaskCounts does not count snoozed tasks which are in the future`() {
        val snoozed = LocalDate.MAX
        val model = DomainModel(listOf(TaskList("", mutableListOf(Task("", snoozed = snoozed)))))
        assertEquals(TaskCounts(0, 0, 0, 0), model.getTaskCounts(TaskListIndex(0)))
    }

    @Test
    fun `getTaskCounts returns proper counts`() {
        val model = DomainModel(
            listOf(
                TaskList(
                    "TaskList1",
                    mutableListOf(
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
                    mutableListOf(
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
        assertEquals(TaskCounts(1, 2, 3, 0), model.getTaskCounts(TaskListIndex(0)))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `getTask throws for invalid taskList index`() {
        val model = DomainModel(listOf())
        model.getTask(TaskListIndex(123), TaskIndex(0))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `getTask throws for invalid task index`() {
        val model = DomainModel(listOf())
        model.getTask(TaskListIndex(0), TaskIndex(123))
    }

    @Test
    fun `getTask works properly for valid indexes`() {
        val model = DomainModel(
            listOf(
                TaskList(
                    "", mutableListOf(
                        Task("1"),
                        Task("2")
                    )
                ),
                TaskList(
                    "", mutableListOf(
                        Task("3")
                    )
                )
            )
        )
        assertEquals(Task("1"), model.getTask(TaskListIndex(0), TaskIndex(0)))
        assertEquals(Task("2"), model.getTask(TaskListIndex(0), TaskIndex(1)))
        assertEquals(Task("3"), model.getTask(TaskListIndex(1), TaskIndex(0)))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `getArchive with invalid taskList index throws`() {
        val model = DomainModel(listOf())
        model.getArchive(TaskListIndex(123))
    }

    @Test
    fun `getArchive empty result on default DomainModel`() {
        val model = DomainModel(listOf())
        assertTrue(model.getArchive(TaskListIndex(0)).isEmpty())
    }

    @Test
    fun `getArchive empty return when no archived tasks`() {
        val model = DomainModel(listOf(TaskList("", mutableListOf(Task("")))))
        assertTrue(model.getArchive(TaskListIndex(0)).isEmpty())
    }

    @Test
    fun `getArchive proper return`() {
        val model = DomainModel(
            listOf(
                TaskList(
                    "",
                    mutableListOf(
                        // 3 Unarchived tasks
                        Task(""),
                        Task(""),
                        Task(""),
                        // 2 Archived tasks
                        Task("", archived = LocalDate.MIN),
                        Task("", archived = LocalDate.MIN)
                    )
                ),
                TaskList(
                    "",
                    mutableListOf(
                        Task(""),
                        Task(""),
                        Task(""),
                    )
                )
            )
        )
        assertEquals(
            listOf(
                IndexedTask(TaskIndex(3), Task("", archived = LocalDate.MIN)),
                IndexedTask(TaskIndex(4), Task("", archived = LocalDate.MIN))
            ),
            model.getArchive(TaskListIndex(0))
        )
        assertTrue(model.getArchive(TaskListIndex(1)).isEmpty())
    }

    @Test
    fun `addTaskList new task list is empty and is named New Task List`() {
        val model = DomainModel(listOf())
        assertTrue(model.taskLists.size == 1)
        val newIndex = model.addTaskList()
        assertEquals(TaskListIndex(1), newIndex)
        val newTaskList = model.taskLists[newIndex.value]
        assertEquals("New Task List", newTaskList.name)
        assertTrue(newTaskList.tasks.isEmpty())
    }

    @Test
    fun `deleteTaskList noop on default DomainModel`() {
        val model = DomainModel(listOf())
        assertFalse(model.deleteTaskList(TaskListIndex(0)))
        assertFalse(model.deleteTaskList(TaskListIndex(1)))
        assertFalse(model.deleteTaskList(TaskListIndex(123)))
        assertFalse(model.deleteTaskList(TaskListIndex(-1)))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `deleteTaskList throws on invalid index`() {
        val model =
            DomainModel(listOf(TaskList("A", mutableListOf()), TaskList("B", mutableListOf())))
        model.deleteTaskList(TaskListIndex(123))
    }

    @Test
    fun `deleteTaskList deletes properly`() {
        val model =
            DomainModel(listOf(TaskList("A", mutableListOf()), TaskList("B", mutableListOf())))
        assertTrue(model.deleteTaskList(TaskListIndex(0)))
        assertEquals(listOf(TaskList("B", mutableListOf())), model.taskLists)
        assertFalse(model.deleteTaskList(TaskListIndex(0)))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `snoozeToTomorrow throws on invalid task list index`() {
        val model = DomainModel(listOf())
        model.snoozeToTomorrow(TaskListIndex(123), TaskIndex(0))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `snoozeToTomorrow throws on invalid task index`() {
        val model = DomainModel(listOf())
        model.snoozeToTomorrow(TaskListIndex(0), TaskIndex(123))
    }

    @Test
    fun `snoozeToTomorrow works properly`() {
        val model = DomainModel(
            listOf(
                TaskList(
                    "A", mutableListOf(
                        Task("1")
                    )
                ),
                TaskList(
                    "B", mutableListOf(
                    )
                ),
            )
        )
        assertEquals(Task("1"), model.getTask(TaskListIndex(0), TaskIndex(0)))
        model.snoozeToTomorrow(TaskListIndex(0), TaskIndex(0))
        val tomorrow = LocalDate.now().plusDays(1)
        assertEquals(Task("1", snoozed = tomorrow), model.getTask(TaskListIndex(0), TaskIndex(0)))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `removeSnooze throws on invalid task list index`() {
        val model = DomainModel(listOf())
        model.removeSnooze(TaskListIndex(123), TaskIndex(0))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `removeSnooze throws on invalid task index`() {
        val model = DomainModel(listOf())
        model.removeSnooze(TaskListIndex(0), TaskIndex(123))
    }

    @Test
    fun `removeSnooze works properly`() {
        val model =
            DomainModel(listOf(TaskList("", mutableListOf(Task("", snoozed = LocalDate.MAX)))))
        assertEquals(
            Task("", snoozed = LocalDate.MAX),
            model.getTask(TaskListIndex(0), TaskIndex(0))
        )
        model.removeSnooze(TaskListIndex(0), TaskIndex(0))
        assertEquals(
            Task("", snoozed = null),
            model.getTask(TaskListIndex(0), TaskIndex(0))
        )
    }

    @Test
    fun `removeSnooze returns old snoozed date`() {
        val model =
            DomainModel(listOf(TaskList("", mutableListOf(Task("", snoozed = LocalDate.MAX)))))
        assertEquals(LocalDate.MAX, model.removeSnooze(TaskListIndex(0), TaskIndex(0)))
        assertNull(model.removeSnooze(TaskListIndex(0), TaskIndex(0)))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `scheduleNext throws on invalid task list index`() {
        val model = DomainModel(listOf())
        model.scheduleNext(TaskListIndex(123), TaskIndex(0))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `scheduleNext throws on invalid task index`() {
        val model = DomainModel(listOf())
        model.scheduleNext(TaskListIndex(0), TaskIndex(123))
    }

    @Test
    fun `scheduleNext works properly`() {
        val schedule = Schedule(ScheduleInXTimeUnits(1, TimeUnit.DAY))
        val model =
            DomainModel(listOf(TaskList("", mutableListOf(Task("", schedule = schedule)))))
        assertEquals(
            Task("", schedule = schedule),
            model.getTask(TaskListIndex(0), TaskIndex(0))
        )
        model.scheduleNext(TaskListIndex(0), TaskIndex(0))
        val nextDate = schedule.scheduleNext(LocalDate.now())
        assertEquals(
            Task("", schedule = schedule, due = nextDate, snoozed = nextDate),
            model.getTask(TaskListIndex(0), TaskIndex(0))
        )
    }

    @Test
    fun `scheduleNext is noop on null schedule`() {
        val model =
            DomainModel(listOf(TaskList("", mutableListOf(Task("", schedule = null)))))
        assertEquals(
            Task("", schedule = null),
            model.getTask(TaskListIndex(0), TaskIndex(0))
        )
        model.scheduleNext(TaskListIndex(0), TaskIndex(0))
        assertEquals(
            Task("", schedule = null),
            model.getTask(TaskListIndex(0), TaskIndex(0))
        )
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `doArchive throws on invalid task list index`() {
        val model = DomainModel(listOf())
        model.doArchive(TaskListIndex(123), TaskIndex(0), LocalDate.MAX)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `doArchive throws on invalid task index`() {
        val model = DomainModel(listOf())
        model.doArchive(TaskListIndex(0), TaskIndex(123), LocalDate.MAX)
    }

    @Test
    fun `doArchive works properly`() {
        val model = DomainModel(listOf(TaskList("", mutableListOf(Task("", archived = null)))))
        assertEquals(
            Task("", archived = null),
            model.getTask(TaskListIndex(0), TaskIndex(0))
        )
        model.doArchive(TaskListIndex(0), TaskIndex(0), LocalDate.MAX)
        assertEquals(
            Task("", archived = LocalDate.MAX),
            model.getTask(TaskListIndex(0), TaskIndex(0))
        )
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `unarchive throws on invalid task list index`() {
        val model = DomainModel(listOf())
        model.unarchive(TaskListIndex(123), TaskIndex(0))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `unarchive throws on invalid task index`() {
        val model = DomainModel(listOf())
        model.unarchive(TaskListIndex(0), TaskIndex(123))
    }

    @Test
    fun `unarchive works properly`() {
        val model =
            DomainModel(listOf(TaskList("", mutableListOf(Task("", archived = LocalDate.MIN)))))
        assertEquals(
            Task("", archived = LocalDate.MIN),
            model.getTask(TaskListIndex(0), TaskIndex(0))
        )
        model.unarchive(TaskListIndex(0), TaskIndex(0))
        assertEquals(
            Task("", archived = null),
            model.getTask(TaskListIndex(0), TaskIndex(0))
        )
    }

    @Test
    fun `unarchive returns old archive date`() {
        val model =
            DomainModel(listOf(TaskList("", mutableListOf(Task("", archived = LocalDate.MIN)))))
        assertEquals(LocalDate.MIN, model.unarchive(TaskListIndex(0), TaskIndex(0)))
        assertNull(model.unarchive(TaskListIndex(0), TaskIndex(0)))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `addTask throws on invalid task list`() {
        val model = DomainModel(listOf())
        model.addTask(TaskListIndex(123), Task(""))
    }

    @Test
    fun addTask() {
        val model = DomainModel(listOf())
        assertTrue(model.taskLists[0].tasks.isEmpty())
        val success = model.addTask(TaskListIndex(0), Task(""))
        assertTrue(success)
        assertEquals(Task(""), model.getTask(TaskListIndex(0), TaskIndex(0)))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `updateTask throws on invalid task list`() {
        val model = DomainModel(listOf())
        model.updateTask(TaskListIndex(123), TaskIndex(0), Task(""))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `updateTask throws on invalid task`() {
        val model = DomainModel(listOf())
        model.updateTask(TaskListIndex(0), TaskIndex(123), Task(""))
    }

    @Test
    fun updateTask() {
        val model = DomainModel(listOf(TaskList("", mutableListOf(Task("A")))))
        assertEquals(Task("A"), model.taskLists[0].tasks[0])
        val success = model.updateTask(TaskListIndex(0), TaskIndex(0), Task("B"))
        assertTrue(success)
        assertEquals(Task("B"), model.taskLists[0].tasks[0])
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `deleteTask throws on invalid task list index`() {
        val model = DomainModel(listOf())
        model.deleteTask(TaskListIndex(123), TaskIndex(0))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `deleteTask throws on invalid task index`() {
        val model = DomainModel(listOf())
        model.deleteTask(TaskListIndex(0), TaskIndex(123))
    }

    @Test
    fun deleteTask() {
        val model = DomainModel(listOf(TaskList("", mutableListOf(Task("ABC")))))
        assertTrue(model.taskLists[0].tasks.size == 1)
        model.deleteTask(TaskListIndex(0), TaskIndex(0))
        assertTrue(model.taskLists[0].tasks.size == 0)
    }

    @Test
    fun `deleteTask returns removed task`() {
        val model = DomainModel(listOf(TaskList("", mutableListOf(Task("ABC")))))
        assertEquals(Task("ABC"), model.deleteTask(TaskListIndex(0), TaskIndex(0)))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `renameTaskList throw on invalid task list index`() {
        val model = DomainModel(listOf())
        model.renameTaskList(TaskListIndex(123), "")
    }

    @Test
    fun renameTaskList() {
        val model = DomainModel(listOf(TaskList("ABC", mutableListOf())))
        assertEquals(TaskList("ABC", mutableListOf()), model.taskLists[0])
        model.renameTaskList(TaskListIndex(0), "DEF")
        assertEquals(TaskList("DEF", mutableListOf()), model.taskLists[0])
    }

    @Test
    fun `getTaskListNames default task list names`() {
        val model = DomainModel(listOf())
        assertEquals(listOf("Tasks"), model.getTaskListNames())
    }

    @Test
    fun getTaskListNames() {
        val model =
            DomainModel(listOf(TaskList("ABC", mutableListOf()), TaskList("DEF", mutableListOf())))
        assertEquals(listOf("ABC", "DEF"), model.getTaskListNames())
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `getTaskListInformation throws on invalid task list`() {
        val model = DomainModel(listOf())
        model.getTaskListInformation(TaskListIndex(123))
    }

    @Test
    fun `getTaskListInformation default information`() {
        val model = DomainModel(listOf())
        assertEquals(
            TaskListInformation("Tasks", TaskCounts(0, 0, 0, 0)), model.getTaskListInformation(
                TaskListIndex(0)
            )
        )
    }

    @Test
    fun getTaskListInformation() {
        val model = DomainModel(
            listOf(
                TaskList("ABC", mutableListOf(Task("", Importance.IMPORTANT))),
                TaskList("DEF", mutableListOf(Task("", Importance.UNIMPORTANT)))
            )
        )
        assertEquals(
            TaskListInformation("ABC", TaskCounts(0, 1, 0, 0)), model.getTaskListInformation(
                TaskListIndex(0)
            )
        )
        assertEquals(
            TaskListInformation("DEF", TaskCounts(0, 0, 0, 1)), model.getTaskListInformation(
                TaskListIndex(1)
            )
        )
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `moveFromTaskListToTaskList throws on invalid taskId`() {
        val model = DomainModel(listOf(TaskList("", mutableListOf(Task("")))))
        model.moveTaskFromListToList(TaskIndex(123), TaskListIndex(0), TaskListIndex(0))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `moveFromTaskListToTaskList throws on invalid fromTaskList`() {
        val model = DomainModel(listOf(TaskList("", mutableListOf(Task("")))))
        model.moveTaskFromListToList(TaskIndex(0), TaskListIndex(123), TaskListIndex(0))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `moveFromTaskListToTaskList throws on invalid toTaskList`() {
        val model = DomainModel(listOf(TaskList("", mutableListOf(Task("")))))
        model.moveTaskFromListToList(TaskIndex(0), TaskListIndex(0), TaskListIndex(123))
    }

    @Test
    fun `moveFromTaskListToTaskList to same list appends task to the end`() {
        val model = DomainModel(listOf(TaskList("", mutableListOf(Task("ABC"), Task("DEF")))))
        assertEquals(TaskList("", mutableListOf(Task("ABC"), Task("DEF"))), model.taskLists[0])
        model.moveTaskFromListToList(TaskIndex(0), TaskListIndex(0), TaskListIndex(0))
        assertEquals(TaskList("", mutableListOf(Task("DEF"), Task("ABC"))), model.taskLists[0])
    }

    @Test
    fun `moveFromTaskListToTaskList properly from list to list`() {
        val model = DomainModel(
            listOf(
                TaskList("1", mutableListOf(Task("ABC"), Task("DEF"))),
                TaskList("2", mutableListOf(Task("GHI"), Task("JKL")))
            )
        )
        assertEquals(TaskList("1", mutableListOf(Task("ABC"), Task("DEF"))), model.taskLists[0])
        assertEquals(TaskList("2", mutableListOf(Task("GHI"), Task("JKL"))), model.taskLists[1])
        model.moveTaskFromListToList(TaskIndex(1), TaskListIndex(0), TaskListIndex(1))
        assertEquals(TaskList("1", mutableListOf(Task("ABC"))), model.taskLists[0])
        assertEquals(
            TaskList("2", mutableListOf(Task("GHI"), Task("JKL"), Task("DEF"))),
            model.taskLists[1]
        )
    }

    @Test
    fun `getUnarchivedTasks returns tasks with snoozed date in the past as unsnoozed`() {
        val yesterday = LocalDate.now().minusDays(1)
        val model = DomainModel(
            listOf(
                TaskList(
                    "", mutableListOf(
                        Task("", Importance.IMPORTANT, due = LocalDate.MAX, snoozed = yesterday)
                    )
                )
            )
        )
        assertEquals(
            UnarchivedTasks(
                unsnoozed = listOf(
                    IndexedTask(
                        TaskIndex(0),
                        Task("", Importance.IMPORTANT, due = LocalDate.MAX, snoozed = yesterday)
                    )
                ),
                snoozed = emptyList()
            ), model.getUnarchivedTasks(TaskListIndex(0), TaskCategory.DO)
        )
    }

    @Test
    fun addSnooze() {
        val model = DomainModel(listOf(TaskList("", mutableListOf(Task("")))))
        assertEquals(Task("", snoozed = null), model.getTask(TaskListIndex(0), TaskIndex(0)))
        model.addSnooze(TaskListIndex(0), TaskIndex(0), LocalDate.MAX)
        assertEquals(
            Task("", snoozed = LocalDate.MAX),
            model.getTask(TaskListIndex(0), TaskIndex(0))
        )
    }

    @Test
    fun `getUnarchivedTasks sorts due tasks by due date`() {
        val model = DomainModel(
            listOf(
                TaskList(
                    "",
                    mutableListOf(
                        Task("A", due = LocalDate.of(2222, 11, 11)),
                        Task("B", due = LocalDate.of(1111, 11, 11))
                    )
                )
            )
        )
        assertEquals(
            UnarchivedTasks(
                listOf(
                    IndexedTask(TaskIndex(1), Task("B", due = LocalDate.of(1111, 11, 11))),
                    IndexedTask(TaskIndex(0), Task("A", due = LocalDate.of(2222, 11, 11)))
                ),
                emptyList()
            ),
            model.getUnarchivedTasks(TaskListIndex(0), TaskCategory.DO)
        )
    }
}
