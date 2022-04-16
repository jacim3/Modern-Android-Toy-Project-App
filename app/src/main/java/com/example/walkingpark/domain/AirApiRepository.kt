package com.example.walkingpark.domain

import com.example.walkingpark.data.source.api.dto.AirDTO


interface AirApiRepository {

    suspend fun startAirApi(query: Map<String, String>): List<AirDTO.Response.Body.Items>?

    fun extractQuery(stationName: String) : Map<String, String>
}