package com.example.walkingpark.data.repository

import com.example.walkingpark.api.PublicApiService
import com.example.walkingpark.domain.model.AirDTO
import com.example.walkingpark.domain.model.StationDTO
import com.example.walkingpark.domain.model.WeatherDTO
import com.example.walkingpark.data.repository.datasoruce.AirApiSource
import com.example.walkingpark.data.repository.datasoruce.StationApiSource
import com.example.walkingpark.data.repository.datasoruce.WeatherApiSource
import com.example.walkingpark.di.module.PublicDataApiModule
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestApiRepository @Inject constructor(
    @PublicDataApiModule.AirAPI
    private val airApi: PublicApiService,
    @PublicDataApiModule.StationAPI
    private val stationApi: PublicApiService,
    @PublicDataApiModule.WeatherApi
    private val weatherApi: PublicApiService,
    private val publicApiKey: String
) {

    // TODO 추후 UseCase 에서 비즈니스 로직을 구현을 위해 사용
    var responseWeatherApi: StationDTO.Response.Body.Items? = null
    var responseAirApi: List<AirDTO.Response.Body.Items>? = null
    var responseStationApi: List<WeatherDTO.Response.Body.Items.Item>? = null

    suspend fun getDataFromStationApi(addressMap: Map<Char, String>, latLng: LatLng): StationDTO.Response.Body.Items? {
        responseWeatherApi = StationApiSource(publicApiKey, airApi, addressMap, latLng).fetchData()
        return responseWeatherApi
    }

    suspend fun getDataFromAirApi(stationName: String): List<AirDTO.Response.Body.Items>? {
        responseAirApi = AirApiSource(publicApiKey, stationApi, stationName).fetchData()
        return responseAirApi
    }

    suspend fun getDataFromWeatherApi(latLng: LatLng): List<WeatherDTO.Response.Body.Items.Item>? {
        responseStationApi = WeatherApiSource(publicApiKey, weatherApi, latLng).fetchData()
        return responseStationApi
    }
}