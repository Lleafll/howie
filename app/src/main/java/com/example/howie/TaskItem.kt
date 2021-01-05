package com.example.howie

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.task_item.view.*
import java.time.LocalDate

class TaskItem : LinearLayout {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    init {
        inflate(context, R.layout.task_item, this)
        val taskManager = TaskManager.getInstance()
        snooze_to_tomorrow.setOnClickListener {
            val updatedTask = task.copy(snoozed = LocalDate.now().plusDays(1))
            updatedTask.id = task.id
            taskManager.update(updatedTask)
        }
        remove_snooze.setOnClickListener {
            val updatedTask = task.copy(snoozed = null)
            updatedTask.id = task.id
            taskManager.update(updatedTask)
        }
        reschedule_button.setOnClickListener {
            val updatedTask = task.scheduleNext()
            if (updatedTask != null) {
                taskManager.update(updatedTask)
            }
        }
        archive_button.setOnClickListener {
            val updatedTask = task.copy(archived = LocalDate.now())
            updatedTask.id = task.id
            taskManager.update(updatedTask)
        }
        unarchive_button.setOnClickListener {
            val updatedTask = task.copy(archived = null)
            updatedTask.id = task.id
            taskManager.update(updatedTask)
        }
        edit_button.setOnClickListener {
            editListener?.invoke(task.id)
        }
        setOnClickListener {
            toggle()
        }
    }

    var task: Task = Task("", 0)
        set(value) {
            field = value
            name_text_view.text = value.name
            setDateString(due_text_view, value.due)
            setDateString(snoozed_text_view, value.snoozed)
            setDateString(archived_text_view, value.archived)
            if (field.snoozed == null || field.snoozed!! <= LocalDate.now()) {
                snooze_to_tomorrow.isVisible = true
                remove_snooze.isVisible = false
            } else {
                snooze_to_tomorrow.isVisible = false
                remove_snooze.isVisible = true
            }
            when {
                value.archived != null -> {
                    reschedule_button.isVisible = false
                    archive_button.isVisible = false
                    unarchive_button.isVisible = true
                }
                value.schedule != null -> {
                    reschedule_button.isVisible = true
                    archive_button.isVisible = false
                    unarchive_button.isVisible = false
                }
                else -> {
                    reschedule_button.isVisible = false
                    archive_button.isVisible = true
                    unarchive_button.isVisible = false
                }
            }
        }

    private var editListener: ((Int) -> Unit)? = null

    fun setEditListener(editListener: (Int) -> Unit) {
        this.editListener = editListener
    }

    private fun toggle() = if (bottom_layout.isVisible) {
        collapse()
    } else {
        expand()
    }

    private fun collapse() {
        bottom_layout.isVisible = false
        name_text_view.isSingleLine = true
        name_text_view.ellipsize = TextUtils.TruncateAt.MARQUEE
    }

    private fun expand() {
        bottom_layout.isVisible = true
        name_text_view.isSingleLine = false
        name_text_view.maxLines = 3
        name_text_view.ellipsize = TextUtils.TruncateAt.END
    }
}

private fun setDateString(view: TextView, date: LocalDate?) {
    if (date == null) {
        view.visibility = View.INVISIBLE
    } else {
        view.isVisible = true
        view.text = date.toString()
    }
}