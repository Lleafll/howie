package com.example.howie

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

enum class Importance {
    IMPORTANT, UNIMPORTANT
}

class Converters {
    @TypeConverter
    fun toImportance(index: Int): Importance = Importance.values()[index]

    @TypeConverter
    fun toInt(importance: Importance): Int = importance.ordinal

    @TypeConverter
    fun toLocalDate(epochDay: Long?): LocalDate? {
        return if (epochDay == null) {
            null
        } else {
            LocalDate.ofEpochDay(epochDay)
        }
    }

    @TypeConverter
    fun toLong(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}

@Parcelize
@Entity
@TypeConverters(Converters::class)
data class Task(
    val name: String,
    val importance: Importance,
    val due: LocalDate,
    val snoozed: LocalDate?,
    val completed: LocalDate?
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}