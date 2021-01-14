package com.example.howie.ui

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.howie.core.Importance
import com.example.howie.core.TaskListIndex
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@ExperimentalCoroutinesApi
class TaskViewModelTest {
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
    fun `construct taskFields with dependency injection`() {
        val application = mockk<Application>(relaxed = true)
        val repository = mockk<TasksRepository>(relaxed = true)
        val viewModel = TaskViewModel(application, repository, null)
    }

    @Test
    fun `taskFields builds default TaskField when supplying null task index and null task category`() {
        val application = mockk<Application>(relaxed = true)
        val repository = mockk<TasksRepository>(relaxed = true)
        val viewModel = TaskViewModel(application, repository, testScope)
        viewModel.initialize(TaskListIndex(0), null, null)
        viewModel.taskFields.observeForever {}
        val expected = TaskFields(
            "",
            Importance.IMPORTANT,
            false,
            todaysString(),
            false,
            todaysString(),
            null
        )
        assertEquals(expected, viewModel.taskFields.value)
    }
}

private fun todaysString() = LocalDate.now().toString()