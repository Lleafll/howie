package com.example.howie.database

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
            listOf(TaskListEntity("ABC"))
        )
        assertEquals(listOf(TaskList("ABC", listOf())), databaseModel.toDomainModel())
    }
}