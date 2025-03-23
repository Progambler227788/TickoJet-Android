package com.talhaatif.tickojet.utils


import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.talhaatif.tickojet.databinding.LoadingDialogBinding

class LoadingDialog(context: Context) {
    private val dialog: Dialog = Dialog(context)
    private val binding: LoadingDialogBinding = LoadingDialogBinding.inflate(LayoutInflater.from(context))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}
