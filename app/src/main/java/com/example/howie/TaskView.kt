package com.example.howie

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.task_view.view.*
import java.time.LocalDate

class TaskView : ConstraintLayout {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    init {
        inflate(context, R.layout.task_view, this)
        snoozeSwitch.setOnCheckedChangeListener { _, isChecked ->
            snoozedTextDate.isVisible = isChecked
        }
        dueSwitch.setOnCheckedChangeListener { _, isChecked ->
            dueTextDate.isVisible = isChecked
        }
    }

    fun setTask(task: Task) {
        taskNameEditText.setText(task.name)
        if(task.importance == Importance.IMPORTANT) {
            importantButton.isChecked = true
        } else {
            unimportantButton.isChecked = true
        }
        setDateFields(dueTextDate, dueSwitch, task.due)
        setDateFields(snoozedTextDate, snoozeSwitch, task.snoozed)
    }

    fun getTask() = Task(
        taskNameEditText.text.toString(),
        if (importantButton.isChecked) Importance.IMPORTANT else Importance.UNIMPORTANT,
        readDate(dueSwitch, dueTextDate),
        readDate(snoozeSwitch, snoozedTextDate)
    )
}

private fun setDateFields(picker: TextView, switch: SwitchCompat, date: LocalDate?) {
    if (date != null) {
        switch.isChecked = true
        picker.isVisible = true
        picker.text = date.toString()
    } else {
        switch.isChecked = false
        picker.isVisible = false
        picker.text = LocalDate.now().toString()
    }
}

private fun readDate(switch: SwitchCompat, picker: EditText): LocalDate? {
    return if (!switch.isChecked) {
        null
    } else {
        LocalDate.now() // TODO(Implement)
    }
}