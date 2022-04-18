package com.example.walkingpark.data.repository

import com.example.walkingpark.data.source.api.PublicApiService
import com.example.walkingpark.di.module.PublicDataApiModule
import com.example.walkingpark.domain.repository.AirApiRepository
import com.example.walkingpark.domain.model.AirDTO
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AirApiRepositoryImpl @Inject constructor(
    private val apiKey: String,
    @PublicDataApiModule.AirAPI
    private val airApi: PublicApiService
): AirApiRepository {

    override suspend fun startAirApi(query: Map<String, String>): Response<AirDTO> {
        return  airApi.getAirDataByStationName(apiKey, query)
    }

    override fun extractQuery(stationName: String): Map<String, String> {

        return mapOf(
            Pair("returnType", "json"),
            Pair("stationName", stationName),
            Pair("dataTerm", "daily")
        )
    }

    override fun handleResponse(response: Response<AirDTO>): List<AirDTO.Response.Body.Items>? {
        try {
            return response.body()?.response?.body?.items
        } catch (e: Exception) { }
        return null
    }
}