package com.example.howie

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Calendar

enum class Importance {
    IMPORTANT, UNIMPORTANT
}

@Parcelize
data class Task(
    val name: String,
    val importance: Importance,
    val due: Calendar,
    val snoozed: Calendar?,
    val completed: Calendar?
) : Parcelable