package com.talhaatif.tickojet.utils


import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.talhaatif.tickojet.databinding.LoadingDialogBinding


class LoadingDialog(context: Context) {
    private val dialog: Dialog = Dialog(context).apply {
        setContentView(LoadingDialogBinding.inflate(LayoutInflater.from(context)).root)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun show() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    fun isShowing(): Boolean = dialog.isShowing
}