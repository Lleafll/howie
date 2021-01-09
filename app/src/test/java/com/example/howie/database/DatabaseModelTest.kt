package com.example.howie.database

import com.example.howie.TaskEntity
import com.example.howie.core.Importance
import com.example.howie.core.Task
import com.example.howie.core.TaskList
import org.junit.Assert.assertEquals
import org.junit.Test

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
        assertEquals(listOf(TaskList("ABC", listOf())), databaseModel.toDomainModel())
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
                TaskList("ABC", listOf()),
                TaskList("DEF", listOf()),
                TaskList("GHI", listOf()),
            ),
            databaseModel.toDomainModel()
        )
    }

    @Test
    fun `toDomainModel from one TaskListEntity and one TaskEntity`() {
        val databaseModel = DatabaseModel(
            listOf(TaskEntity("TaskName", 123, Importance.IMPORTANT, null, null, null, null, null)),
            listOf(TaskListEntity("ABC", 123))
        )
        assertEquals(
            listOf(
                TaskList(
                    "ABC",
                    listOf(Task("TaskName", Importance.IMPORTANT, null, null, null, null))
                )
            ), databaseModel.toDomainModel()
        )
    }

    @Test
    fun `toDomainModel from several TaskListEntities and one TaskEntity`() {
        val databaseModel = DatabaseModel(
            listOf(TaskEntity("TaskName", 123, Importance.IMPORTANT, null, null, null, null, null)),
            listOf(TaskListEntity("ABC", 123), TaskListEntity("DEF", 456))
        )
        assertEquals(
            listOf(
                TaskList(
                    "ABC",
                    listOf(Task("TaskName", Importance.IMPORTANT, null, null, null, null))
                ),
                TaskList(
                    "DEF",
                    listOf()
                )
            ), databaseModel.toDomainModel()
        )
    }
}