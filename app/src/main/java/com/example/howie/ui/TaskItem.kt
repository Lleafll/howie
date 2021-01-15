package com.example.howie.ui

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.howie.R
import com.example.howie.databinding.TaskItemBinding

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
        fun onSelected()
    }

    private val binding = TaskItemBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        inflate(context, R.layout.task_item, this)
        binding.snoozeToTomorrow.setOnClickListener {
            _listener?.onSnoozeToTomorrowClicked()
        }
        binding.removeSnooze.setOnClickListener {
            _listener?.onRemoveSnoozeClicked()
        }
        binding.rescheduleButton.setOnClickListener {
            _listener?.onRescheduleClicked()
        }
        binding.archiveButton.setOnClickListener {
            _listener?.onArchiveClicked()
        }
        binding.unarchiveButton.setOnClickListener {
            _listener?.onUnarchiveClicked()
        }
        binding.editButton.setOnClickListener {
            _listener?.onEditClicked()
        }
        setOnClickListener {
            _listener?.onSelected()
        }
    }

    private var _listener: Listener? = null

    fun setFields(fields: TaskItemFields) {
        binding.nameTextView.text = fields.name
        setDateString(binding.dueTextView, fields.due)
        setDateString(binding.snoozedTextView, fields.snoozed)
        setDateString(binding.archivedTextView, fields.archived)
        binding.snoozeToTomorrowLayout.isVisible = fields.snoozedToTomorrow
        binding.removeSnoozeLayout.isVisible = fields.removeSnoozed
        binding.rescheduleLayout.isVisible = fields.reschedule != null
        if (fields.reschedule != null) {
            binding.scheduleText.text = fields.reschedule
        }
        binding.archiveLayout.isVisible = fields.archive
        binding.unarchiveLayout.isVisible = fields.unarchive
    }

    fun setListener(listener: Listener) {
        _listener = listener
    }

    fun isExpanded(): Boolean = binding.bottomLayout.isVisible

    fun collapse() {
        binding.bottomLayout.isVisible = false
        binding.nameTextView.isSingleLine = true
        binding.nameTextView.ellipsize = TextUtils.TruncateAt.MARQUEE
    }

    fun expand() {
        binding.bottomLayout.isVisible = true
        binding.nameTextView.isSingleLine = false
        binding.nameTextView.maxLines = 3
        binding.nameTextView.ellipsize = TextUtils.TruncateAt.END
    }
}

private fun setDateString(view: TextView, date: String?) {
    if (date == null) {
        view.visibility = View.INVISIBLE
    } else {
        view.isVisible = true
        view.text = date
    }
}