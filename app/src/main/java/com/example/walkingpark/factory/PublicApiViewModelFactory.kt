package com.example.walkingpark.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.walkingpark.MainViewModel
import com.example.walkingpark.repository.RestApiRepository


class PublicApiViewModelFactory(private val application: RestApiRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application) as T
    }
}