package com.example.howie

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TasksObjectViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksObjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasksObjectViewModel(application) as T
        }
        throw IllegalArgumentException("Unable to construct ViewModel in $this")
    }
}