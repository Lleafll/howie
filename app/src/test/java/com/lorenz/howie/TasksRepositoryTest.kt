package com.lorenz.howie

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lorenz.howie.core.Importance
import com.lorenz.howie.core.TaskIndex
import com.lorenz.howie.core.TaskListIndex
import com.lorenz.howie.database.TaskDao
import com.lorenz.howie.database.TaskEntity
import com.lorenz.howie.database.TaskListDao
import com.lorenz.howie.database.TaskListEntity
import com.lorenz.howie.ui.TasksRepository
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TasksRepositoryTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
        testScope.cleanupTestCoroutines()
    }

    @Test
    fun `deleteTask saves to database`() {
        val taskDao = mockk<TaskDao>(relaxed = true) {
            every { getAll() } returns listOf(
                TaskEntity(
                    "ABC",
                    0,
                    Importance.IMPORTANT,
                    null,
                    null,
                    null,
                    null,
                    null,
                    0
                )
            )
        }
        val taskListDao = mockk<TaskListDao>(relaxed = true) {
            every { getAllTaskLists() } returns listOf(TaskListEntity("DEF", 0))
        }
        val repository = TasksRepository({ }, taskDao, taskListDao)
        runBlocking {
            repository.deleteTask(TaskIndex(TaskListIndex(0), 0))
        }
        coVerify {
            taskDao.insertAll(emptyList())
            taskListDao.insertAll(listOf(TaskListEntity("DEF", 0)))
        }
    }
}