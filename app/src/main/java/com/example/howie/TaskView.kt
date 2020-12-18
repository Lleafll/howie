package com.example.howie

import android.content.Context
import android.util.AttributeSet
import android.widget.DatePicker
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
        readDate(dueSwitch, duePicker),
        readDate(snoozeSwitch, snoozePicker)
    )
}

private fun setDateFields(picker: DatePicker, switch: SwitchCompat, date: LocalDate?) {
    if (date != null) {
        switch.isChecked = true
        picker.updateDate(date.year, date.monthValue - 1, date.dayOfMonth)
    } else {
        switch.isChecked = false
    }
}

private fun readDate(switch: SwitchCompat, picker: DatePicker): LocalDate? {
    return if (!switch.isChecked) {
        null
    } else {
        LocalDate.of(picker.year, picker.month + 1, picker.dayOfMonth)
    }
}