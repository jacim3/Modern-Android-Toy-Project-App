package com.example.walkingpark.components.ui.dialog

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import com.example.walkingpark.R
import com.example.walkingpark.data.enum.Common

class LoadingIndicator(private val activity: Activity) {
    lateinit var dialog: AlertDialog

    fun startLoadingIndicator() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.ui_loading_indicator, null))
        builder.setCancelable(true)
        dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun dismissIndicator() {

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, Common.LOADING_INDICATOR_DISMISS_TIME.toLong())
    }
}