package com.example.walkingpark.di.repository

import android.util.Log
import com.example.walkingpark.di.module.ApiKeyModule
import com.example.walkingpark.di.module.PublicDataApiModule
import com.example.walkingpark.data.dto.AirDTO
import com.example.walkingpark.data.dto.StationDTO
import com.example.walkingpark.data.dto.WeatherDTO
import com.example.walkingpark.data.tool.LatLngToGridXy
import com.example.walkingpark.retrofit2.PublicApiService
import retrofit2.Call
import retrofit2.Response
import java.sql.Timestamp
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

    @PublicDataApiModule.WeatherApi
    @Inject
    lateinit var weatherApiL: PublicApiService

    @ApiKeyModule.PublicApiKey
    @Inject
    lateinit var publicApiKey: String

    lateinit var userStationItem: StationDTO.Response.Body.Items
    lateinit var userAirInfoItem: List<AirDTO.Response.Body.Items>
    lateinit var userWeatherItem: String


    // 측정소 이름으로 데이터 가져오기 -> 주소값을 텍스트로 도 시 구 군 동 읍 면 기준으로 가져오므로.. 여러번 검색해야 할듯?
    suspend fun getStationDataBySIName(siName: String): Response<StationDTO>? {
        if (publicApiKey != "") {

            // TODO 우선 '시' 를 기준으로 뽑겠음. 예외처리가 필요하면, 나중에...

            return stationApi.getStationDataByName(publicApiKey, "json", siName)
        }
        return null
    }
    // 미세먼지 등급은 1~4
    // 공기 데이터 측정소 이름으로 가져오기
    suspend fun getAirDataByStationName(stationName: String): Response<AirDTO>? {
        if (publicApiKey != "") {
            return airApi.getAirDataByStationName(publicApiKey, "json", stationName, "DAILY")
        }
        return null
    }


    // TODO 데이터는 모두 올바르게 서버로 보내나, HTTP 500 Internal Server Error 발생.
    suspend fun getWeatherDataByGridXy(latitude:Double, longitude:Double): Response<WeatherDTO.Response.Body.Items>? {

        val stamp = Timestamp(System.currentTimeMillis())
        val dateTime = stamp.toString().replace("-", "").replace(":", "").split(".")[0].split(" ")
        val date = dateTime[0]
        val time = dateTime[1]
        var hour = time.substring(0, 2).toInt()
        var minute = time.substring(2, 4)
        var hourMinute = ""
        if (minute.length == 1) {
            minute = "0$minute"
        }

        hour -= 1
        hourMinute =
            if (hour < 10) {
                "0$hour$minute"
            } else {
                "$hour$minute"
            }


        val grid = LatLngToGridXy(latitude, longitude)

        if (publicApiKey != "") {
            return airApi.getWeatherByGridXY(publicApiKey, "json", date, hourMinute, 1000, grid.locX, grid.locY)
        }
        return null
    }
}