package com.example.howie

import android.content.Context
import android.util.AttributeSet
import java.util.*

class TaskItem : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)

    var task: Task = Task("", Importance.IMPORTANT, Calendar.getInstance(), null, null)
        set(value) {
            field = value
            text = "${value.name}\nDue: ${toDateString(value.due)}\nSnoozed: ${toDateString(value.snoozed)}"
        }
}

private fun toDateString(date: Calendar?): String {
    return if (date == null) {
        ""
    } else {
        "${date.get(Calendar.DAY_OF_MONTH)}.${date.get(Calendar.MONTH)}.${date.get(Calendar.YEAR)}"
    }
}