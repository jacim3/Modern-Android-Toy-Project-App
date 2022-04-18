package com.example.walkingpark.domain.repository

import android.location.Address
import com.example.walkingpark.domain.model.StationDTO
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response


interface StationApiRepository {


    fun extractQuery(addresses: List<Address>): Map<String, String>

    suspend fun startStationApi(
        query: Map<String, String>,
        latLng: LatLng
    ): Response<StationDTO>

    fun handleResponse(response: Response<StationDTO>) :  List<StationDTO.Response.Body.Items>?

    fun extractNearStationByLatLng(
        items: List<StationDTO.Response.Body.Items>?,
        latLng: LatLng
    ): StationDTO.Response.Body.Items?


}