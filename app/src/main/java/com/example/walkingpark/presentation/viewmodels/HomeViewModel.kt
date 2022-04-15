package com.example.walkingpark.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.walkingpark.data.repository.RestApiRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/*
    TODO 현재는 UI 관련 비즈니스 로직을 작성하지 않았으므로 사용하지 않음.
*/

@HiltViewModel
class HomeViewModel @Inject constructor(private val restApiRepository: RestApiRepository) : ViewModel(
) {


    // TODO 데이터는 모두 올바르게 서버로 보내나, HTTP 500 Internal Server Error 발생.
    // TODO 동네예보 조회서비스는 일단 보류.
    suspend fun getDataFromWeatherAPI(latLng: LatLng) {
        restApiRepository.getDataFromWeatherApi(latLng)
    }


    // 측정소별 디테일한 측정결과를 받기 위해서는 반드시 측정소 정보를 얻어온 후, 미세먼지 측정이 가능하므로,
    // 이 메서드는 observe 를 통하여 수행.
    suspend fun getDataFromAirAPI(stationName: String) {

    }
}