package com.example.howie

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_schedule_for_next_day_of_month.view.*

class ScheduleForNextDayOfMonthView(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_schedule_for_next_day_of_month, this)
    }

    fun setSchedule(schedule: ScheduleForNextDayOfMonth) =
        day_of_month_field.setText(schedule.dayOfMonth)

    fun getSchedule() = ScheduleForNextDayOfMonth(day_of_month_field.text.toString().toInt())
}