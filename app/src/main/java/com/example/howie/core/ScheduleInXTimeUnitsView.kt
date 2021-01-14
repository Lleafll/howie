package com.example.howie.core

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.example.howie.R
import kotlinx.android.synthetic.main.view_schedule_in_x_time_units.view.*

class ScheduleInXTimeUnitsView(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_schedule_in_x_time_units, this)
        ArrayAdapter.createFromResource(
            context,
            R.array.time_units,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            time_unit_spinner.adapter = adapter
        }
    }

    fun setSchedule(schedule: ScheduleInXTimeUnits) {
        quantity_field.setText(schedule.quantity.toString())
        time_unit_spinner.setSelection(schedule.timeUnit.ordinal)
    }

    fun getSchedule() = ScheduleInXTimeUnits(
        convertTextToValidQuantity(quantity_field.text),
        TimeUnit.values()[time_unit_spinner.selectedItemPosition]
    )
}

private fun convertTextToValidQuantity(text: Editable): Long = try {
    text.toString().toLong().coerceAtLeast(1)
} catch (e: NumberFormatException) {
    1
}