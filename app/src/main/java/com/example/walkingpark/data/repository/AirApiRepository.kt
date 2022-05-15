package com.example.walkingpark.data.repository

import com.example.walkingpark.data.source.ApiDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AirApiRepository @Inject constructor(
    private val apiDataSource: ApiDataSource
){

    fun startAirApi(stationName:String) = apiDataSource.getAirApi(getAirQuery(stationName))

    private fun getAirQuery(stationName: String) = mapOf(
        Pair("returnType", "json"),
        Pair("stationName", stationName),
        Pair("dataTerm", "daily")
    )
}