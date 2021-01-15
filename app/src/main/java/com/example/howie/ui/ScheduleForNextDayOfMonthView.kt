package com.example.howie.ui

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.howie.R
import com.example.howie.core.ScheduleForNextDayOfMonth
import kotlinx.android.synthetic.main.view_schedule_for_next_day_of_month.view.*

class ScheduleForNextDayOfMonthView(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_schedule_for_next_day_of_month, this)
    }

    fun setSchedule(schedule: ScheduleForNextDayOfMonth) {
        day_of_month_field.setText(schedule.dayOfMonth.toString())
    }

    fun getSchedule() =
        ScheduleForNextDayOfMonth(convertTextToValidDayOfMonth(day_of_month_field.text))
}

private fun convertTextToValidDayOfMonth(text: Editable): Int = try {
    text.toString().toInt().coerceIn(1, 31)
} catch (e: NumberFormatException) {
    1
}