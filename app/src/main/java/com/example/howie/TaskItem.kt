package com.example.howie

import android.content.Context
import android.util.AttributeSet
import java.util.*

class TaskItem : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)

    fun setTask(task: Task) {
        text = "${task.name}\nDue: ${toDateString(task.due)}\nSnoozed: ${toDateString(task.snoozed)}"
    }
}

private fun toDateString(date: Calendar?): String {
    return if (date == null) {
        "";
    } else {
        "${date.get(Calendar.DAY_OF_MONTH)}.${date.get(Calendar.MONTH)}.${date.get(Calendar.YEAR)}"
    }
}