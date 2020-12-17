package com.example.howie

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.Calendar

enum class Importance {
    IMPORTANT, UNIMPORTANT
}

@Parcelize
@Entity
data class Task(
    @PrimaryKey val id: Int,
    val name: String,
    val importance: Importance,
    val due: Calendar,
    val snoozed: Calendar?,
    val completed: Calendar?
) : Parcelable {
}