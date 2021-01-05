package com.example.howie

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText

class RenameTaskListFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val messageBoxBuilder =
            AlertDialog.Builder(requireActivity()).setView(R.layout.fragment_rename_task_list)
                .setMessage("Rename List")
                .setPositiveButton("Ok") { _, _ ->
                    val textEdit: TextInputEditText = dialog!!.findViewById(R.id.new_task_list_name)
                    val taskManager = TaskManager.getInstance(requireActivity().application)
                    taskManager.renameCurrentTaskList(textEdit.text.toString())
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
        val dialog = messageBoxBuilder.create()
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return dialog
    }
}