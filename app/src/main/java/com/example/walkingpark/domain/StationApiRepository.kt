package com.example.walkingpark.domain

import android.location.Address
import com.example.walkingpark.data.source.api.dto.StationDTO
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response


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