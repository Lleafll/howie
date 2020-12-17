package com.example.howie

import android.content.Context
import android.util.AttributeSet
import java.time.LocalDate

class TaskItem : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    var task: Task = Task("", Importance.IMPORTANT, LocalDate.now(), null, null)
        set(value) {
            field = value
            text =
                "${value.name}\nDue: ${toDateString(value.due)}\nSnoozed: ${toDateString(value.snoozed)}"
        }
}

private fun toDateString(date: LocalDate?): String {
    return date?.toString() ?: ""
}