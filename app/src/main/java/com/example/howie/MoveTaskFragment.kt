package com.example.howie

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer

class MoveTaskFragment : DialogFragment() {
    private lateinit var listener: MoveTaskFragmentListener

    interface MoveTaskFragmentListener {
        fun onTaskMoved()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val taskManager = TaskManager.getInstance(requireActivity().application)
        val taskListIds = mutableListOf<Long>()
        val messageBoxBuilder =
            AlertDialog.Builder(requireActivity()).setView(R.layout.fragment_move_task)
                .setMessage("Move Task")
                .setPositiveButton("Ok") { _, _ ->
                    val spinner: Spinner = dialog!!.findViewById(R.id.task_list_spinner)
                    val taskId = requireArguments().getInt("taskId")
                    val selectedIndex = spinner.selectedItemPosition
                    taskManager.moveToList(taskId, taskListIds[selectedIndex])
                    listener.onTaskMoved()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
        val dialog = messageBoxBuilder.create()
        taskManager.taskLists.observe(this, Observer {
            val spinner: Spinner = this.dialog!!.findViewById(R.id.task_list_spinner)
            val nameList = mutableListOf<String>()
            it.map { taskList -> taskList.name }
            for (taskList in it) {
                if (taskList.id != taskManager.currentTaskListId) {
                    nameList.add(taskList.name)
                    taskListIds.add(taskList.id)
                }
            }
            val adapter = ArrayAdapter<String>(
                requireActivity(),
                android.R.layout.simple_spinner_item,
                nameList
            )
            spinner.adapter = adapter
        })
        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as MoveTaskFragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement MoveTaskFragmentListener")
        }
    }
}