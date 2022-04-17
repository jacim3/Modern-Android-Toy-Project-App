package com.example.walkingpark.domain.repository

import com.example.walkingpark.domain.model.tools.LatLngToGridXy
import com.example.walkingpark.data.source.api.dto.WeatherDTO
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response

interface WeatherApiRepository {

    suspend fun startWeatherApi(query: Map<String, String>): Response<WeatherDTO>

    fun getQuery(timeMap:Map<String, String>, grid:LatLngToGridXy) : Map<String, String>

    fun getTimeForQuery() : Map<String, String>

    fun changeLatLngToGrid(latLng: LatLng) : LatLngToGridXy

    fun handleResponse(response: Response<WeatherDTO>) : List<WeatherDTO.Response.Body.Items.Item>?
}