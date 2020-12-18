package com.example.howie

import android.content.Context
import android.util.AttributeSet
import android.widget.CalendarView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.task_view.view.*
import java.time.LocalDate

class TaskView : ConstraintLayout {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    init {
        inflate(context, R.layout.task_view, this)
        snoozeSwitch.setOnCheckedChangeListener { _, isChecked ->
            snoozePicker.isVisible = isChecked
        }
        dueSwitch.setOnCheckedChangeListener { _, isChecked ->
            duePicker.isVisible = isChecked
        }
    }

    fun setTask(task: Task) {
        taskNameEditText.setText(task.name)
        setDateFields(duePicker, dueSwitch, task.due)
        setDateFields(snoozePicker, snoozeSwitch, task.snoozed)
    }

    fun getTask() = Task(
        taskNameEditText.text.toString(),
        Importance.IMPORTANT,
        readDateFromPicker(duePicker),
        readDateFromPicker(snoozePicker)
    )
}

private fun setDateFields(picker: CalendarView, switch: SwitchCompat, date: LocalDate?) {
    if (date != null) {
        switch.isChecked = true
        picker.date = date.toEpochDay()
    } else {
        switch.isChecked = false
        picker.isVisible = false
    }
}

private fun readDateFromPicker(picker: CalendarView): LocalDate? {
    return if (picker.isInvisible) {
        null
    } else {
        LocalDate.ofEpochDay(picker.date)
    }
}