package com.example.howie

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_schedule_in_x_time_units.view.*

class ScheduleInXTimeUnitsView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_schedule_in_x_time_units, this)
        ArrayAdapter.createFromResource(
            getContext(),
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
        quantity_field.text.toString().toLong(),
        TimeUnit.values()[time_unit_spinner.selectedItemPosition]
    )
}