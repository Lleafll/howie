package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TasksTabViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksTabViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasksTabViewModel(application) as T
        }
        throw IllegalArgumentException("Unable to construct ViewModel in $this")
    }
}