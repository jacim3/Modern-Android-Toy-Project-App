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


// TODO 브로드캐스트 리시버 관련 로직 삭제 및 위치서비스 관련 로직을 프래그먼트로 이동.
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {


    val userLocation = MutableLiveData<LocationEntity>()
    val locationObservable = MutableLiveData<Flowable<LocationObject>>()
    val userLocationHistory = HashMap<Long, LocationEntity>()  // 사용자 경로 기록

    // 리액티브 Handler
    @SuppressLint("CheckResult")
    fun locationObservableHandler(){
        locationObservable.value
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { loc ->
                userLocation.value = LocationEntity(loc.latitude, loc.longitude).apply {
                    userLocationHistory[loc.time] = this
                }
            }
    }
}
