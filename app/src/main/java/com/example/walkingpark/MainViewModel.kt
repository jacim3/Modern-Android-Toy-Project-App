package com.example.walkingpark

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkingpark.components.background.service.ParkMapsService
import com.example.walkingpark.dto.AirDTO
import com.example.walkingpark.dto.ParkDTO
import com.example.walkingpark.dto.StationTmDTO
import com.example.walkingpark.repository.PublicDataApiRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: PublicDataApiRepository) : ViewModel() {

    val responseStationTmDataSet = MutableLiveData<Response<StationTmDTO>>()
    val responseAirDataSet = MutableLiveData<Response<AirDTO>>()

    fun serviceStart(context: Context) {

        // 보안을 위해서, 서비스는 반드시 명시적 인텐트를 사용해야 한다.
        val intent = Intent(context,ParkMapsService::class.java)
        // 포그라운드 서비스 버전 처리
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            context
            .startForegroundService(intent)
        else
            //context.bindService(intent)
    }

    fun serviceStop(context: Context) {
        val intent = Intent(context, ParkMapsService::class.java)
        context.stopService(intent)
    }
}