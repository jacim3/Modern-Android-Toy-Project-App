package com.example.walkingpark.presentation.viewmodels

import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.walkingpark.constants.Settings
import com.example.walkingpark.data.source.api.dto.AirDTO
import com.example.walkingpark.data.source.api.dto.StationDTO
import com.example.walkingpark.data.source.api.dto.WeatherDTO
import com.example.walkingpark.domain.usecase.api.air.parent.ResultAirUseCase
import com.example.walkingpark.domain.usecase.api.station.parent.ResultStationUseCase
import com.example.walkingpark.domain.usecase.api.weather.parent.ResultWeatherUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

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
    val userLiveHolderWeather = MutableLiveData<List<WeatherDTO.Response.Body.Items.Item>>()
    val userLiveHolderLoadedStatus = MutableLiveData<MutableMap<String, String>>().apply {
        this.postValue(
            mapOf(
                Pair("station", "fail"),
                Pair("air", "fail"),
                Pair("weather", "fail")
            ) as HashMap<String, String>?
        )
    }

    suspend fun startStationApi(latLng: LatLng) {

        viewModelScope.launch {
            val response = stationUseCase(getGeocoding(latLng), latLng)
            response?.let {
                userLiveHolderStation.postValue(it)
                startAirApi(it.stationName)
                val tmpMap = userLiveHolderLoadedStatus.value
                tmpMap!!["station"] = "success"
                userLiveHolderLoadedStatus.postValue(tmpMap)
            }
        }
    }

    private fun getGeocoding(latLng: LatLng): List<Address> {
        val coder = Geocoder(getApplication(), Locale.getDefault())
        return coder.getFromLocation(
            latLng.latitude,
            latLng.longitude,
            Settings.LOCATION_ADDRESS_SEARCH_COUNT
        )
    }

    private suspend fun startAirApi(stationName: String) {
        viewModelScope.launch {
            val response = airUseCase(stationName)

            response?.let {
                userLiveHolderAir.postValue(it)
                val tmpMap = userLiveHolderLoadedStatus.value
                tmpMap!!["air"] = "success"
                userLiveHolderLoadedStatus.postValue(tmpMap)
            }
        }
    }

    suspend fun startWeatherApi(latLng: LatLng) {
        viewModelScope.launch {
            val response = weatherUseCase(latLng)
            response?.let {
                userLiveHolderWeather.value = it
                val tmpMap = userLiveHolderLoadedStatus.value
                tmpMap!!["weather"] = "success"
                userLiveHolderLoadedStatus.postValue(tmpMap)
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// ----------------------------------------- DataBinding -------------------------------------------
// -------------------------------------------------------------------------------------------------
