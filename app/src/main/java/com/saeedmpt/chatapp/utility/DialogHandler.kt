package com.saeedmpt.chatapp.utility

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager

class DialogHandler {
    fun createMDialog(layoutRes: Int, context: Context?): Dialog {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(layoutRes)
        val window = dialog.window
        window!!.attributes.gravity = Gravity.BOTTOM
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        //window.getAttributes().windowAnimations = R.style.DialogAnimation; //style id
        dialog.show()
        return dialog
    }

    fun createMDialogUnClose(layoutRes: Int, context: Context?): Dialog {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(layoutRes)
        val window = dialog.window
        window!!.attributes.gravity = Gravity.CENTER
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        //window.getAttributes().windowAnimations = R.style.DialogAnimation; //style id
        dialog.show()
        return dialog
    }
}