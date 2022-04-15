package com.example.walkingpark.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationReceiverRepository @Inject constructor() {

    val mData = MediatorLiveData<String>()

    fun addDataSource(data: LiveData<String>) {
        mData.addSource(data, mData::setValue)
    }

    fun removeDataSource(data: LiveData<String>) {
        mData.removeSource(data)
    }
    fun getData(): LiveData<String> {
        return mData
    }
}