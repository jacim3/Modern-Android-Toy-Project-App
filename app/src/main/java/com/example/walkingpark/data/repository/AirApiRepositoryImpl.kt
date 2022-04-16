package com.example.walkingpark.data.repository

import com.example.walkingpark.data.source.api.PublicApiService
import com.example.walkingpark.di.module.PublicDataApiModule
import com.example.walkingpark.domain.AirApiRepository
import com.example.walkingpark.data.source.api.dto.AirDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AirApiRepositoryImpl @Inject constructor(
    private val apiKey: String,
    @PublicDataApiModule.AirAPI
    private val airApi: PublicApiService
): AirApiRepository {

    private var responseAirApi: List<AirDTO.Response.Body.Items>? = null

    override suspend fun startAirApi(query: Map<String, String>): List<AirDTO.Response.Body.Items>? {

        val response = airApi.getAirDataByStationName(apiKey, query)
        if (response.isSuccessful) {
            return response.body()?.response?.body?.items
        }
        return null
    }

    override fun extractQuery(stationName: String): Map<String, String> {
        val queryMap = HashMap<String, String>().apply {
            this["returnType"] = "json"
            this["stationName"] = stationName
            this["dataTerm"] = "DAILY"
        }
        return queryMap
    }
}