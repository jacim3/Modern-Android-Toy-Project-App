package com.example.walkingpark.view

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import androidx.appcompat.widget.AppCompatTextView
import com.example.walkingpark.R
import com.example.walkingpark.data.enum.Common

class LoadingIndicator(private val activity: Activity, private val text:String) {
    lateinit var dialog: AlertDialog

    var flag = "None"           // 특정 목적에 따라 다이얼로를 구분하기 위한 변수.

    fun startLoadingIndicator() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.ui_loading_indicator, null))
        builder.setCancelable(true)
        dialog = builder.create()
        dialog.show()
        dialog.findViewById<AppCompatTextView>(R.id.textViewDescription).text = text
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun dismissIndicator() {

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, Common.LOADING_INDICATOR_DISMISS_TIME.toLong())
    }

    fun setDescription(text:String){
        dialog.findViewById<AppCompatTextView>(R.id.textViewDescription).text = text
    }
}