package com.example.howie

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WidgetConfigureViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WidgetConfigureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WidgetConfigureViewModel(application) as T
        }
        throw IllegalArgumentException("Unable to construct ViewModel in $this")
    }
}