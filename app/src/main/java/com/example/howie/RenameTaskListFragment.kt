package com.example.howie

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText

class RenameTaskListFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val messageBoxBuilder =
            AlertDialog.Builder(activity!!).setView(R.layout.fragment_rename_task_list)
                .setMessage("Rename List")
                .setPositiveButton("Ok") { _, _ ->
                    val textEdit: TextInputEditText = dialog!!.findViewById(R.id.new_task_list_name)
                    val taskManager = TaskManager.getInstance(activity!!.applicationContext)
                    taskManager.renameCurrentTaskList(textEdit.text.toString())
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
        return messageBoxBuilder.create()
    }
}