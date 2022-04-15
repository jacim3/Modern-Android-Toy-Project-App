package com.example.walkingpark.domain

import com.example.walkingpark.data.tools.LatLngToGridXy
import com.example.walkingpark.domain.model.WeatherDTO
import com.google.android.gms.maps.model.LatLng

interface WeatherApiRepository {

    suspend fun startWeatherApi(query: Map<String, String>): List<WeatherDTO.Response.Body.Items.Item>?

    fun extractQuery(timeMap:Map<String, String>, grid:LatLngToGridXy) : Map<String, String>

    fun extractTime() : Map<String, String>

    fun changeLatLngToGrid(latLng: LatLng) : LatLngToGridXy
}