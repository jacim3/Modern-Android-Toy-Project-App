package com.example.walkingpark.presentation.viewmodels

import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.walkingpark.constants.Settings
import com.example.walkingpark.data.source.api.dto.AirDTO
import com.example.walkingpark.data.source.api.dto.StationDTO
import com.example.walkingpark.data.source.api.dto.WeatherDTO
import com.example.walkingpark.domain.usecase.api.air.child.GetAirUseCase
import com.example.walkingpark.domain.usecase.api.air.parent.ResultAirUseCase
import com.example.walkingpark.domain.usecase.api.station.child.GetStationUseCase
import com.example.walkingpark.domain.usecase.api.station.parent.ResultStationUseCase
import com.example.walkingpark.domain.usecase.api.weather.child.GetWeatherUseCase
import com.example.walkingpark.domain.usecase.api.weather.parent.ResultWeatherUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/*
    TODO 현재는 UI 관련 비즈니스 로직을 작성하지 않았으므로 사용하지 않음.
*/

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val weatherUseCase: ResultWeatherUseCase,
    private val airUseCase: ResultAirUseCase,
    private val stationUseCase: ResultStationUseCase,
) : AndroidViewModel(application) {

    val userLiveHolderStation = MutableLiveData<StationDTO.Response.Body.Items?>()
    val userLiveHolderAir = MutableLiveData<List<AirDTO.Response.Body.Items>?>()
    val userLiveHolderWeather = MutableLiveData<
            List<WeatherDTO.Response.Body.Items.Item>>()

    suspend fun startStationApi(latLng: LatLng) {
        viewModelScope.launch {
            val coder = Geocoder(getApplication(), Locale.getDefault())
            val location: List<Address> =
                coder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    Settings.LOCATION_ADDRESS_SEARCH_COUNT
                )
            val response = stationUseCase(location, latLng)

            Log.e("coder", response.toString())

            response?.let {
                userLiveHolderStation.postValue(it)
            }
        }
    }

    suspend fun startWeatherApi(latLng: LatLng) {
        viewModelScope.launch {
            val response = weatherUseCase(latLng)
            if (!response.isNullOrEmpty()) {
                Log.e("weatherApi : ", response.size.toString())
                response.let {
                    userLiveHolderWeather.postValue(it)
                }
            }
        }
    }

    suspend fun startAirApi(stationName: String) {
        viewModelScope.launch {
            val response = airUseCase(stationName)
            if (!response.isNullOrEmpty()) {
                response.let {
                    userLiveHolderAir.postValue(it)
                }
            }
        }
    }
}