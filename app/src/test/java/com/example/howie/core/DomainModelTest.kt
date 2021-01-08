package com.example.howie.core

import org.junit.Assert.assertTrue
import org.junit.Test

class DomainModelTest {
    @Test
    fun `getTaskListInformation return empty list when tasks are empty`() {
        val model = DomainModel(listOf())
        assertTrue(model.getTaskListInformation().isEmpty())
    }
}
