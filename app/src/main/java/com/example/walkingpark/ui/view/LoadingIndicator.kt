package com.example.walkingpark.ui.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatTextView
import com.example.walkingpark.R
import com.example.walkingpark.constants.Common


class LoadingIndicator(context: Context, private val text:String) {

    var dialog: AlertDialog
    var flag = "None"           // 특정 목적에 따라 다이얼로를 구분하기 위한 변수.

    init {
        val builder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        builder.setView(inflater.inflate(R.layout.ui_loading_indicator, null))
        builder.setCancelable(true)
        dialog = builder.create()
    }

    fun startLoadingIndicator() {

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