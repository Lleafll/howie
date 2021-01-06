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
import java.time.LocalDate
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
        every { repository.currentTaskListId } returns MutableLiveData(123)
        val taskListNamesAndCounts = taskManager.getTaskListNamesAndCounts()
        taskListNamesAndCounts.observeForever {
            assertEquals(listOf(TaskListNameAndCount(123, "ABC", TaskCounts(0, 0, 0, 0), true)), it)
        }
    }

    @Test
    fun `getTaskListNamesAndCounts with two lists and no tasks`() {
        val application = mockk<Application>(relaxed = true)
        val repository = mockk<TasksRepository>(relaxed = true)
        val taskManager = TaskManager(application, repository)
        every { repository.taskLists } returns MutableLiveData(
            listOf(
                TaskList("ABC", 123),
                TaskList("DEF", 456)
            )
        )
        every { repository.currentTaskListId } returns MutableLiveData(123)
        val taskListNamesAndCounts = taskManager.getTaskListNamesAndCounts()
        taskListNamesAndCounts.observeForever {
            assertEquals(
                listOf(
                    TaskListNameAndCount(123, "ABC", TaskCounts(0, 0, 0, 0), true),
                    TaskListNameAndCount(456, "DEF", TaskCounts(0, 0, 0, 0), false)
                ), it
            )
        }
    }

    @Test
    fun `getTaskListNamesAndCounts with one list and one count`() {
        val application = mockk<Application>(relaxed = true)
        val repository = mockk<TasksRepository>(relaxed = true)
        val taskManager = TaskManager(application, repository)
        every { repository.taskLists } returns MutableLiveData(listOf(TaskList("ABC", 123)))
        every { repository.currentTaskListId } returns MutableLiveData(123)
        every { repository.tasks } returns MutableLiveData(
            listOf(
                // 1 Do task
                Task("", 123, Importance.IMPORTANT, LocalDate.now()),
                // 2 Decide tasks
                Task("", 123, Importance.IMPORTANT),
                Task("", 123, Importance.IMPORTANT),
                // 3 Delegate Tasks
                Task("", 123, Importance.UNIMPORTANT, LocalDate.now()),
                Task("", 123, Importance.UNIMPORTANT, LocalDate.now()),
                Task("", 123, Importance.UNIMPORTANT, LocalDate.now())
            )
        )
        val taskListNamesAndCounts = taskManager.getTaskListNamesAndCounts()
        taskListNamesAndCounts.observeForever {
            assertEquals(listOf(TaskListNameAndCount(123, "ABC", TaskCounts(1, 2, 3, 0), true)), it)
        }
    }
}
