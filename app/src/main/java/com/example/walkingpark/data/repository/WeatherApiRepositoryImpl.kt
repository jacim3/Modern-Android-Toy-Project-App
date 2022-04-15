package com.example.walkingpark.data.repository

import android.util.Log
import com.example.walkingpark.data.repository.datasoruce.WeatherApiSource
import com.example.walkingpark.data.source.api.PublicApiService
import com.example.walkingpark.data.tools.LatLngToGridXy
import com.example.walkingpark.di.module.PublicDataApiModule
import com.example.walkingpark.domain.WeatherApiRepository
import com.example.walkingpark.domain.model.StationDTO
import com.example.walkingpark.domain.model.WeatherDTO
import com.google.android.gms.maps.model.LatLng
import java.sql.Timestamp
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WeatherApiRepositoryImpl @Inject constructor(
    private val apiKey:String,
    @PublicDataApiModule.WeatherApi
    private val weatherApi: PublicApiService
) :WeatherApiRepository {

    private var responseWeatherApi: StationDTO.Response.Body.Items? = null

    override suspend fun startWeatherApi(query: Map<String, String>, ): List<WeatherDTO.Response.Body.Items.Item>? {

        Log.e("111111111111111111", query.toString())
        val response =  weatherApi.getWeatherByGridXY(apiKey, query)
        if (response.isSuccessful) {
            return response.body()?.response?.body?.items?.item
        }
        return null
    }

    override fun extractQuery(timeMap: Map<String, String>, grid:LatLngToGridXy): Map<String, String> {

        val queryMap = HashMap<String, String>().apply {
            this["dataType"] = "json"
            this["base_date"] = timeMap["date"].toString()
            this["base_time"] = timeMap["time"].toString()
            this["numOfRows"] = "1000"
            this["nx"] = grid.locX.toString()
            this["ny"] = grid.locX.toString()
        }

        return queryMap
    }

    override fun extractTime(): Map<String, String> {
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
        val timeMap = HashMap<String, String>()
        timeMap.apply {
            this["date"] = date
            this["time"] = hourMinute
        }
        return timeMap
    }

    override fun changeLatLngToGrid(latLng: LatLng): LatLngToGridXy {
        return LatLngToGridXy(latLng.latitude,latLng.longitude)
    }
}