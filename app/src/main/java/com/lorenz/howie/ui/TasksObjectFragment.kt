package com.lorenz.howie.ui

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.lorenz.howie.databinding.FragmentTasksObjectBinding
import com.lorenz.howie.core.TaskCategory
import com.lorenz.howie.core.TaskIndex

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
        _binding.tasksView.layoutManager = LinearLayoutManager(context)
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
    val liveData = getLiveDataAccordingToCategory(viewModel)
    binding.tasksView.itemAnimator = ExpandableItemAnimator()
    liveData.observe(viewLifecycleOwner) { unarchivedTasks ->
        val adapters = mutableListOf<TaskAdapter>()
        if (unarchivedTasks.unsnoozed.isNotEmpty()) {
            adapters.add(
                buildTaskAdapter(
                    viewModel,
                    activity,
                    "Due Tasks",
                    unarchivedTasks.unsnoozed,
                    true
                )
            )
        }
        if (unarchivedTasks.snoozed.isNotEmpty()) {
            adapters.add(
                buildTaskAdapter(
                    viewModel,
                    activity,
                    "Snoozed Tasks",
                    unarchivedTasks.snoozed,
                    false
                )
            )
        }
        val concatAdapterConfig = ConcatAdapter.Config.Builder()
            .setIsolateViewTypes(false)
            .build()
        binding.tasksView.adapter = ConcatAdapter(concatAdapterConfig, adapters)
    }
}

private fun TasksObjectFragment.getLiveDataAccordingToCategory(viewModel: MainViewModel): LiveData<UnarchivedTaskItemFields> {
    return when (arguments!!.getSerializable(TasksObjectFragment.TASK_CATEGORY_ARGUMENT)!! as TaskCategory) {
        TaskCategory.DO -> viewModel.doTasks
        TaskCategory.DECIDE -> viewModel.decideTasks
        TaskCategory.DELEGATE -> viewModel.delegateTasks
        TaskCategory.DROP -> viewModel.dropTasks
    }
}

private fun buildTaskAdapter(
    viewModel: MainViewModel,
    activity: FragmentActivity,
    headerTitle: String,
    tasks: List<TaskItemFields>,
    isInitiallyExpanded: Boolean
) =
    TaskAdapter(
        tasks,
        headerTitle,
        object : TaskAdapter.Listener {
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
                intent.putExtra(TaskActivity.TASK_LIST_INDEX, index.list)
                activity.startActivityForResult(intent, TASK_ACTIVITY_REQUEST_CODE)
            }
        }, isInitiallyExpanded
    )