package com.example.howie

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
class TaskManagerTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val mainThreadSurrogate = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun add() {
        val application = mockk<Application>(relaxed = true)
        val repository = mockk<TasksRepository>(relaxed = true)
        val taskManager = TaskManager(application, repository)
        taskManager.doArchive(123)
        coVerify { repository.doArchive(123) }
    }

    @Test
    fun `getTaskListNamesAndCounts with one list and no tasks`() {
        val application = mockk<Application>(relaxed = true)
        val repository = mockk<TasksRepository>(relaxed = true)
        val taskManager = TaskManager(application, repository)
        every { repository.taskLists } returns MutableLiveData(listOf(TaskList("ABC", 123)))
        val taskListNamesAndCounts = taskManager.getTaskListNamesAndCounts()
        taskListNamesAndCounts.observeForever {
            assertEquals(listOf(TaskListNameAndCount(123, "ABC", 0)), it)
        }
    }
}
