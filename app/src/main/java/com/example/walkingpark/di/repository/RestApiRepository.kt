package com.example.walkingpark.di.repository

import android.content.Context
import com.example.walkingpark.di.module.ApiKeyModule
import com.example.walkingpark.di.module.PublicDataApiModule
import com.example.walkingpark.dto.AirDTO
import com.example.walkingpark.dto.StationNameDTO
import com.example.walkingpark.dto.StationTmDTO
import com.example.walkingpark.retrofit2.PublicApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestApiRepository @Inject constructor() {


    @PublicDataApiModule.AirAPI
    @Inject
    lateinit var airApi: PublicApiService

    @PublicDataApiModule.StationAPI
    @Inject
    lateinit var stationApi: PublicApiService

    @ApiKeyModule.PublicApiKey
    @Inject
    lateinit var publicApiKey: String

    // 측정소 이름으로 데이터 가져오기 -> 주소값을 텍스트로 도 시 구 군 동 읍 면 기준으로 가져오므로.. 여러번 검색해야 할듯?
    suspend fun getStationDataByName(stationName: String): Response<StationNameDTO>? {
        if (publicApiKey != "") {
            return stationApi.getStationDataByName(publicApiKey, "json", stationName)
        }
        return null
    }

    // 측정소 tm 좌표로 데이터 가져오기
    suspend fun getStationDataByTm(tmX: String, tmY: String): Response<StationTmDTO>? {
        if (publicApiKey != "") {
            return stationApi.getStationDataByTmxTmy(publicApiKey, "json", tmX, tmY)
        }
        return null
    }

    // 공기 데이터 측정소 이름으로 가져오기
    suspend fun getAirDataByStationName(stationName: String): Response<AirDTO>? {
        if (publicApiKey != "") {
            return airApi.getAirDataByStationName(publicApiKey, "json", stationName)
        }
        return null
    }

}