package com.example.howie.ui

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.howie.R
import com.example.howie.core.Task
import kotlinx.android.synthetic.main.task_item.view.*
import java.time.LocalDate

class TaskItem : LinearLayout {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    interface Listener {
        fun onSnoozeToTomorrowClicked()
        fun onRemoveSnoozeClicked()
        fun onRescheduleClicked()
        fun onArchiveClicked()
        fun onUnarchiveClicked()
        fun onEditClicked()
    }

    init {
        inflate(context, R.layout.task_item, this)
        snooze_to_tomorrow.setOnClickListener {
            _listener?.onSnoozeToTomorrowClicked()
        }
        remove_snooze.setOnClickListener {
            _listener?.onRemoveSnoozeClicked()
        }
        reschedule_button.setOnClickListener {
            _listener?.onRescheduleClicked()
        }
        archive_button.setOnClickListener {
            _listener?.onArchiveClicked()
        }
        unarchive_button.setOnClickListener {
            _listener?.onUnarchiveClicked()
        }
        edit_button.setOnClickListener {
            _listener?.onEditClicked()
        }
        setOnClickListener {
            toggle()
        }
    }

    private var _listener: Listener? = null

    var task: Task = Task("")
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

    fun setListener(listener: Listener) {
        _listener = listener
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