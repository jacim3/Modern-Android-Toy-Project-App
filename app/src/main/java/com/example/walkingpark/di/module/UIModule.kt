package com.example.walkingpark.di.module

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.walkingpark.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UIModule {

    @Provides
    fun provideLoadingIndicator(@ApplicationContext context: Context): AlertDialog? {
        val builder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        builder.setView(inflater.inflate(R.layout.ui_loading_indicator, null))
        builder.setCancelable(true)
        return builder.create()
    }
}