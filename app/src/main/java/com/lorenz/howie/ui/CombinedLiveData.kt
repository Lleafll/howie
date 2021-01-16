package com.lorenz.howie.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class CombinedLiveData<T1, T2>(data1: LiveData<T1>, data2: LiveData<T2>) :
    MediatorLiveData<Pair<T1, T2>>() {

    init {
        var value1: T1? = null
        var value2: T2? = null
        val onChanged = {
            if (value1 != null && value2 != null) {
                value = Pair(value1!!, value2!!)
            }
        }
        addSource(data1) {
            value1 = it
            onChanged()
        }
        addSource(data2) {
            value2 = it
            onChanged()
        }
    }
}