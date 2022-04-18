package com.example.walkingpark.domain.repository

import com.example.walkingpark.domain.model.AirDTO
import retrofit2.Response


interface AirApiRepository {

    suspend fun startAirApi(query: Map<String, String>): Response<AirDTO>

    fun extractQuery(stationName: String) : Map<String, String>

    fun handleResponse(response: Response<AirDTO>) : List<AirDTO.Response.Body.Items>?
}