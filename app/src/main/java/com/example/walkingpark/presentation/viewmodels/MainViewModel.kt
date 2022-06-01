package com.example.walkingpark.presentation.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.walkingpark.data.model.entity.LocationEntity
import com.example.walkingpark.data.model.entity.LocationObject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject


// TODO Location Tracker Logic
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    val userLocation = MutableLiveData<LocationEntity>()
    val locationObservable = MutableLiveData<Flowable<LocationObject>>()
    val userLocationHistory = HashMap<Long, LocationEntity>()  // 사용자 경로 기록

    // 리액티브 Handler
    @SuppressLint("CheckResult")
    fun locationObservableHandler(){
        locationObservable.value?.let {
            it.subscribeOn(Schedulers.computation())
            it.observeOn(AndroidSchedulers.mainThread())
            it.subscribe { loc ->
                userLocation.value = LocationEntity(loc.latitude, loc.longitude).apply {
                    userLocationHistory[loc.time] = this
                }
            }
        }
    }
}
