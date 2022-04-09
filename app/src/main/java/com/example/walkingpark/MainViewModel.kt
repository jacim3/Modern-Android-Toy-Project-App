package com.example.walkingpark

import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.walkingpark.components.foreground.service.ParkMapsService
import com.example.walkingpark.dto.AirDTO
import com.example.walkingpark.dto.StationTmDTO
import com.example.walkingpark.di.repository.RestApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var restApiRepository:RestApiRepository

    val responseStationTmDataSet = MutableLiveData<Response<StationTmDTO>>()
    val responseAirDataSet = MutableLiveData<Response<AirDTO>>()
    val userAddressMap = MutableLiveData<MutableMap<Char, String>>()  // 사용자 위치에 대한 주소데이터 저장

    fun getParkMapsService(service:IBinder): ParkMapsService {

        val mb: ParkMapsService.LocalBinder = service as ParkMapsService.LocalBinder
        return mb.getService() // 서비스가 제공하는 메소드 호출하여 서비스쪽 객체를 전달받을수 있슴
    }

/*    // 위치정보 퍼미션 관련
    // ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION 처리
    fun handleLocationPermissions(permissions : MutableMap<String, Boolean>?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when {
                permissions!!.getOrDefault(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    false
                ) -> {
                    // Precise location access granted.
                    return true
                }
                permissions.getOrDefault(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    false
                ) -> {
                    // Only approximate location access granted.
                    return true
                }
                else -> {
                    // No location access granted.
                    return false
                }
            }
        }
        return false
    }*/
}