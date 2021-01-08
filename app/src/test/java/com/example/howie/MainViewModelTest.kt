package com.example.howie

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.howie.ui.MainViewModel
import com.example.howie.ui.TasksRepository
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.mockk
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
class MainViewModelTest {
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
    fun doArchive() {
        val application = mockk<Application>(relaxed = true)
        val repository = mockk<TasksRepository>(relaxed = true)
        val taskManager = MainViewModel(application, repository)
        taskManager.setTaskList(456)
        taskManager.doArchive(123)
        coVerify { repository.doArchive(456, 123) }
    }
}
