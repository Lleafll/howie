package com.example.howie

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TaskManagerFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskManager::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskManager(app) as T
        }
        throw IllegalArgumentException("Unable to construct TaskManager in TaskManagerFactory")
    }
}