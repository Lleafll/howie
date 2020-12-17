package com.example.howie

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

enum class Importance {
    IMPORTANT, UNIMPORTANT
}

class Converters {
    @TypeConverter
    fun toImportance(index: Int): Importance = Importance.values()[index]

    @TypeConverter
    fun toInt(importance: Importance): Int = importance.ordinal

    @TypeConverter
    fun toCalendar(serialized: String): Calendar {
        val calendar = Calendar.getInstance()
        val parser = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        calendar.time = parser.parse(serialized)!!
        return calendar
    }

    @TypeConverter
    fun toString(calendar: Calendar): String = calendar.toString()
}

@Parcelize
@Entity
@TypeConverters(Converters::class)
data class Task(
    val name: String,
    val importance: Importance,
    val due: Calendar,
    val snoozed: Calendar?,
    val completed: Calendar?
) : Parcelable {
    @PrimaryKey var id: Int = 0
}