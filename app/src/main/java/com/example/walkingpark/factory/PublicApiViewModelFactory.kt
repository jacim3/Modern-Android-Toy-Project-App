package com.example.walkingpark.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.walkingpark.MainViewModel
import com.example.walkingpark.repository.PublicApiRepository


class PublicApiViewModelFactory(private val application: PublicApiRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application) as T
    }
}