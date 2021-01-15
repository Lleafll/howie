package com.example.howie.ui

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.howie.core.ScheduleForNextDayOfMonth
import com.example.howie.databinding.ViewScheduleForNextDayOfMonthBinding

class ScheduleForNextDayOfMonthView(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private val binding =
        ViewScheduleForNextDayOfMonthBinding.inflate(LayoutInflater.from(context), this, true)

    fun setSchedule(schedule: ScheduleForNextDayOfMonth) {
        binding.dayOfMonthField.setText(schedule.dayOfMonth.toString())
    }

    fun getSchedule() =
        ScheduleForNextDayOfMonth(convertTextToValidDayOfMonth(binding.dayOfMonthField.text))
}

private fun convertTextToValidDayOfMonth(text: Editable): Int = try {
    text.toString().toInt().coerceIn(1, 31)
} catch (e: NumberFormatException) {
    1
}