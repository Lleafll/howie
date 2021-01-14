package com.example.howie.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.example.howie.R
import com.example.howie.core.IndexedTask
import com.example.howie.core.TaskCategory
import com.example.howie.core.TaskIndex
import com.example.howie.core.UnarchivedTasks
import kotlinx.android.synthetic.main.fragment_tasks_object.*

class TasksObjectFragment : Fragment(R.layout.fragment_tasks_object) {
    companion object {
        const val TASK_CATEGORY_ARGUMENT = "taskCategory"
    }

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(viewModel, requireActivity())
    }
}

private fun TasksObjectFragment.setupViews(viewModel: MainViewModel, activity: FragmentActivity) {
    unsnoozed_tasks_view.setHeaderText("Tasks")
    snoozed_tasks_view.setHeaderText("Snoozed Tasks")
    val unsnoozedAdapter = buildTaskAdapter(viewModel, activity)
    val snoozedAdapter = buildTaskAdapter(viewModel, activity)
    val liveData = getLiveDataAccordingToCategory(viewModel)
    liveData.observe(viewLifecycleOwner) { unarchivedTasks ->
        setTasks(unsnoozedAdapter, unsnoozed_tasks_view, unarchivedTasks.unsnoozed, true)
        setTasks(snoozedAdapter, snoozed_tasks_view, unarchivedTasks.snoozed, false)
    }
}

private fun TasksObjectFragment.getLiveDataAccordingToCategory(viewModel: MainViewModel): LiveData<UnarchivedTasks> {
    val category =
        arguments!!.getSerializable(TasksObjectFragment.TASK_CATEGORY_ARGUMENT)!! as TaskCategory
    return when (category) {
        TaskCategory.DO -> viewModel.doTasks
        TaskCategory.DECIDE -> viewModel.decideTasks
        TaskCategory.DELEGATE -> viewModel.delegateTasks
        TaskCategory.DROP -> viewModel.dropTasks
    }
}

private fun setTasks(
    taskAdapter: TaskAdapter,
    view: ExpandableTasksView,
    tasks: List<IndexedTask>,
    defaultExpandState: Boolean
) {
    view.setAdapter(taskAdapter)
    if (tasks.isEmpty()) {
        view.visibility = View.GONE
    } else {
        view.visibility = View.VISIBLE
        view.setExpanded(defaultExpandState)
        taskAdapter.submitList(tasks)
    }
}

private fun buildTaskAdapter(viewModel: MainViewModel, activity: FragmentActivity) =
    TaskAdapter(object : TaskAdapter.Listener {
        override fun onSnoozeToTomorrowClicked(index: TaskIndex) {
            viewModel.snoozeToTomorrow(index)
        }

        override fun onRemoveSnoozeClicked(index: TaskIndex) {
            viewModel.removeSnooze(index)
        }

        override fun onRescheduleClicked(index: TaskIndex) {
            viewModel.reschedule(index)
        }

        override fun onArchiveClicked(index: TaskIndex) {
            viewModel.doArchive(index)
        }

        override fun onUnarchiveClicked(index: TaskIndex) {
            viewModel.unarchive(index)
        }

        override fun onEditClicked(index: TaskIndex) {
            val intent = Intent(activity.applicationContext, TaskActivity::class.java)
            intent.putExtra(TaskActivity.TASK_ID, index)
            intent.putExtra(TaskActivity.TASK_LIST_INDEX, viewModel.currentTaskList)
            activity.startActivityForResult(intent, TASK_ACTIVITY_REQUEST_CODE)
        }
    })