package com.example.howie.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ConcatAdapter
import com.example.howie.core.TaskCategory
import com.example.howie.core.TaskIndex
import com.example.howie.databinding.FragmentTasksObjectBinding

class TasksObjectFragment : Fragment() {
    companion object {
        const val TASK_CATEGORY_ARGUMENT = "taskCategory"
    }

    private val _viewModel: MainViewModel by activityViewModels()
    private lateinit var _binding: FragmentTasksObjectBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksObjectBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(_viewModel, requireActivity(), _binding)
    }
}

private fun TasksObjectFragment.setupViews(
    viewModel: MainViewModel,
    activity: FragmentActivity,
    binding: FragmentTasksObjectBinding
) {
    binding.unsnoozedTasksView.setHeaderText("Tasks")
    binding.snoozedTasksView.setHeaderText("Snoozed Tasks")
    val unsnoozedAdapter = buildTaskAdapter(viewModel, activity)
    val snoozedAdapter = buildTaskAdapter(viewModel, activity)
    val liveData = getLiveDataAccordingToCategory(viewModel)
    liveData.observe(viewLifecycleOwner) { unarchivedTasks ->
        setTasks(unsnoozedAdapter, unarchivedTasks.unsnoozed)
        setTasks(snoozedAdapter, unarchivedTasks.snoozed)
        val concatAdapterConfig = ConcatAdapter.Config.Builder()
            .setIsolateViewTypes(false)
            .build()
        val concatAdapter =
            ConcatAdapter(concatAdapterConfig, listOf(unsnoozedAdapter, snoozedAdapter))
        binding.unsnoozedTasksView.setAdapter(concatAdapter)
    }
}

private fun TasksObjectFragment.getLiveDataAccordingToCategory(viewModel: MainViewModel): LiveData<UnarchivedTaskItemFields> {
    val category =
        arguments!!.getSerializable(TasksObjectFragment.TASK_CATEGORY_ARGUMENT)!! as TaskCategory
    return when (category) {
        TaskCategory.DO -> viewModel.doTasks
        TaskCategory.DECIDE -> viewModel.decideTasks
        TaskCategory.DELEGATE -> viewModel.delegateTasks
        TaskCategory.DROP -> viewModel.dropTasks
    }
}

private fun setTasks(taskAdapter: TaskAdapter, tasks: List<TaskItemFields>) {
    taskAdapter.submitList(tasks)
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