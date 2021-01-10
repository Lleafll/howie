package com.example.howie.database

import com.example.howie.TaskEntity
import com.example.howie.core.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.Month

class DatabaseModelTest {
    @Test
    fun `toDomainModel returns empty model on empty mode`() {
        val databaseModel = DatabaseModel(listOf(), listOf())
        assertEquals(listOf<TaskList>(), databaseModel.toDomainModel())
    }

    @Test
    fun `toDomainModel from one TaskListEntity and no TaskEntities`() {
        val databaseModel = DatabaseModel(
            listOf(),
            listOf(TaskListEntity("ABC", 1))
        )
        assertEquals(listOf(TaskList("ABC", mutableListOf())), databaseModel.toDomainModel())
    }

    @Test
    fun `toDomainModel from several TaskListEntities and no TaskEntities`() {
        val databaseModel = DatabaseModel(
            listOf(),
            listOf(
                TaskListEntity("ABC", 1),
                TaskListEntity("DEF", 2),
                TaskListEntity("GHI", 3)
            )
        )
        assertEquals(
            listOf(
                TaskList("ABC", mutableListOf()),
                TaskList("DEF", mutableListOf()),
                TaskList("GHI", mutableListOf()),
            ),
            databaseModel.toDomainModel()
        )
    }

    @Test
    fun `toDomainModel from one TaskListEntity and one TaskEntity`() {
        val databaseModel = DatabaseModel(
            listOf(
                TaskEntity(
                    "TaskName",
                    123,
                    Importance.IMPORTANT,
                    null,
                    null,
                    null,
                    null,
                    null,
                    1
                )
            ),
            listOf(TaskListEntity("ABC", 123))
        )
        assertEquals(
            listOf(
                TaskList(
                    "ABC",
                    mutableListOf(Task("TaskName", Importance.IMPORTANT, null, null, null, null))
                )
            ), databaseModel.toDomainModel()
        )
    }

    @Test
    fun `toDomainModel from several TaskListEntities and one TaskEntity`() {
        val databaseModel = DatabaseModel(
            listOf(
                TaskEntity(
                    "TaskName",
                    123,
                    Importance.IMPORTANT,
                    null,
                    null,
                    null,
                    null,
                    null,
                    1
                )
            ),
            listOf(
                TaskListEntity("ABC", 123),
                TaskListEntity("DEF", 456)
            )
        )
        assertEquals(
            listOf(
                TaskList(
                    "ABC",
                    mutableListOf(Task("TaskName", Importance.IMPORTANT, null, null, null, null))
                ),
                TaskList(
                    "DEF",
                    mutableListOf()
                )
            ), databaseModel.toDomainModel()
        )
    }

    @Test
    fun `toDomainModel check TaskEntity conversion`() {
        val due = LocalDate.of(1234, Month.FEBRUARY, 12)
        val snoozed = LocalDate.of(423, Month.DECEMBER, 4)
        val schedule = Schedule(ScheduleInXTimeUnits(12, TimeUnit.WEEK))
        val completed = LocalDate.of(234, Month.AUGUST, 23)
        val archived = LocalDate.of(4535, Month.JUNE, 7)
        val databaseModel = DatabaseModel(
            listOf(
                TaskEntity(
                    "Task2",
                    123,
                    Importance.UNIMPORTANT,
                    due,
                    snoozed,
                    schedule,
                    completed,
                    archived,
                    1
                )
            ),
            listOf(TaskListEntity("", 123))
        )
        val expected = listOf(
            TaskList(
                "",
                mutableListOf(
                    Task(
                        "Task2",
                        Importance.UNIMPORTANT,
                        due,
                        snoozed,
                        schedule,
                        archived
                    )
                )
            )
        )
        assertEquals(expected, databaseModel.toDomainModel())
    }

    @Test
    fun `toDomainModel tasks which have invalid taskListId are ignored`() {
        val databaseModel = DatabaseModel(
            listOf(
                TaskEntity("TaskName1", 123, Importance.IMPORTANT, null, null, null, null, null, 1),
                TaskEntity("TaskName2", 789, Importance.IMPORTANT, null, null, null, null, null, 2)
            ),
            listOf(
                TaskListEntity("ABC", 123),
                TaskListEntity("DEF", 456)
            )
        )
        assertEquals(
            listOf(
                TaskList(
                    "ABC",
                    mutableListOf(Task("TaskName1", Importance.IMPORTANT, null, null, null, null))
                ),
                TaskList(
                    "DEF",
                    mutableListOf()
                )
            ), databaseModel.toDomainModel()
        )
    }

    @Test
    fun `toDatabaseModel returns empty model on empty model`() {
        val domainModel = listOf<TaskList>()
        assertEquals(DatabaseModel(listOf(), listOf()), domainModel.toDatabaseModel())
    }

    @Test
    fun `toDatabaseModel with only one task list`() {
        val domainModel = listOf(TaskList("ABC", mutableListOf()))
        assertEquals(
            DatabaseModel(listOf(), listOf(TaskListEntity("ABC", 0))),
            domainModel.toDatabaseModel()
        )
    }

    @Test
    fun `toDatabaseModel with one task list and task`() {
        val domainModel = listOf(TaskList("ABC", mutableListOf(Task("DEF"))))
        assertEquals(
            DatabaseModel(
                listOf(TaskEntity("DEF", 0, Importance.IMPORTANT, null, null, null, null, null, 0)),
                listOf(TaskListEntity("ABC", 0))
            ),
            domainModel.toDatabaseModel()
        )
    }
}