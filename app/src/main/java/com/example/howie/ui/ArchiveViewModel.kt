package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.howie.core.IndexedTask
import com.example.howie.database.getDatabase

class ArchiveViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TasksRepository

    init {
        val database = getDatabase(application.applicationContext)
        repository = TasksRepository(database.getTaskDao(), database.getTaskListDao())
    }

    private val _archive = MutableLiveData<List<IndexedTask>>()
    val archive: LiveData<List<IndexedTask>> by this::_archive

    fun refreshArchive() {
        // TODO: Implement refreshing of data
    }
}