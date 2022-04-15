package com.example.walkingpark.domain

import android.location.Address
import com.example.walkingpark.domain.model.AirDTO
import com.example.walkingpark.domain.model.StationDTO
import com.example.walkingpark.domain.model.WeatherDTO
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton


interface StationApiRepository {

    suspend fun startStationApi(
        query: Map<String, String>,
        latLng: LatLng
    ): Response<StationDTO>

    fun extractQuery(addresses: List<Address>): Map<String, String>

    fun extractNearStationByLatLng(
        response: Response<StationDTO>,
        latLng: LatLng
    ): StationDTO.Response.Body.Items?
}