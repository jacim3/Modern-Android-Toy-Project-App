package com.example.walkingpark.data.repository.datasoruce

import com.example.walkingpark.api.PublicApiService
import com.example.walkingpark.domain.model.AirDTO

class AirApiSource(
    private val apiKey:String,
    private val api: PublicApiService,
    private val stationName:String
) {

    suspend fun fetchData(): List<AirDTO.Response.Body.Items>? {

        val response = api.getAirDataByStationName(apiKey, getQuery())
        if (response.isSuccessful) {
            return response.body()?.response?.body?.items
        }
        return null
    }

    private fun getQuery(): HashMap<String, String> {
        val queryMap = HashMap<String, String>().apply {
            this["returnType"] = "json"
            this["stationName"] = stationName
            this["dataTerm"] = "DAILY"
        }
        return queryMap
    }

}