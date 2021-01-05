package com.example.howie

import android.app.Application
import org.junit.Test
import org.mockito.Mockito

class TaskManagerTest {
    @Test
    fun `Combined TaskList names and TaskCounts`() {
        val application = Mockito.mock(Application::class.java)
        val repository = Mockito.mock(TasksRepository::class.java)
        val taskManager = TaskManager(application, repository)
    }
}
