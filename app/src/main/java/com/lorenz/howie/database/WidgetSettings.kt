package com.lorenz.howie.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WidgetSettings(
    @PrimaryKey
    val widgetId: Int,
    val taskListId: Int
)