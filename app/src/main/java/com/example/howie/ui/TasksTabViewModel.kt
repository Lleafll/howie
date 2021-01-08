package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.howie.database.getDatabase
import kotlin.properties.Delegates

data class TabLabels(
    val label0: String,
    val label1: String,
    val label2: String,
    val label3: String
)

class TasksTabViewModel(application: Application) : AndroidViewModel(application) {
    private val _repository: TasksRepository

    init {
        val database = getDatabase(application.applicationContext)
        _repository = TasksRepository(database.getTaskDao(), database.getTaskListDao())
    }

    private var _taskList by Delegates.notNull<Int>()
    private val _labels = MutableLiveData<TabLabels>()
    val labels: LiveData<TabLabels> by this::_labels

    fun initialize(taskList: Int) {
        _taskList = taskList
        refresh()
    }

    private fun refresh() {
        val taskCounts = _repository.getTaskCounts(_taskList)
        _labels.value = TabLabels(
            formatLabel(taskCounts.doCount, "Do"),
            formatLabel(taskCounts.decideCount, "Decide"),
            formatLabel(taskCounts.delegateCount, "Delegate"),
            formatLabel(taskCounts.dropCount, "Drop")
        )
    }
}

private fun formatLabel(taskCount: Int, lowerText: String): String {
    val upperText = if (taskCount != 0) taskCount.toString() else "✓"
    return "$upperText\n$lowerText"
}