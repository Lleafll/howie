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
}