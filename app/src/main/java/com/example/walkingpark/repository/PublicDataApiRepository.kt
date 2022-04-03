package com.example.walkingpark.repository

import android.content.pm.PackageManager
import android.widget.Toast
import com.example.walkingpark.MainActivity
import com.example.walkingpark.dto.AirDTO
import com.example.walkingpark.dto.ParkDTO
import com.example.walkingpark.dto.StationNameDTO
import com.example.walkingpark.dto.StationTmDTO
import com.example.walkingpark.retrofit2.InstanceAirApi
import com.example.walkingpark.retrofit2.InstanceParkApi
import com.example.walkingpark.retrofit2.InstanceStationApi
import retrofit2.Response

class PublicDataApiRepository(private val activity: MainActivity) {

    private val apiKey = checkApiKey(activity)

    // 공원 데이터 가져오기
/*
    suspend fun getParkData(): Response<ParkDTO>? {

        if (apiKey == null) {
            Toast.makeText(activity.applicationContext, "키 없음", Toast.LENGTH_SHORT).show()
            return null
        }
        return InstanceParkApi.api.getParkData(apiKey, "1", "json")
    }*/

    // 측정소 이름으로 데이터 가져오기
    suspend fun getStationDataByName(stationName: String): Response<StationNameDTO>? {
        if (apiKey == null) {
            Toast.makeText(activity.applicationContext, "키 없음", Toast.LENGTH_SHORT).show()
            return null
        }
        return InstanceStationApi.api.getStationDataByName(apiKey, "json", stationName)
    }

    // 측정소 tm 좌표로 데이터 가져오기
    suspend fun getStationDataByTm(tmX: String, tmY: String): Response<StationTmDTO>? {
        if (apiKey == null) {
            Toast.makeText(activity.applicationContext, "키 없음", Toast.LENGTH_SHORT).show()
            return null
        }
        return InstanceStationApi.api.getStationDataByTmxTmy(apiKey, "json", tmX, tmY)
    }

    // 공기 데이터 측정소 이름으로 가져오기
    suspend fun getAirDataByStationName(stationName: String): Response<AirDTO>? {
        if (apiKey == null) {
            Toast.makeText(activity.applicationContext, "키 없음", Toast.LENGTH_SHORT).show()
            return null
        }
        return InstanceAirApi.api.getAirDataByStationName(apiKey, "json", stationName)
    }

    private fun checkApiKey(activity: MainActivity): String? {
        try {
            val metaSet = activity.packageManager.getApplicationInfo(
                activity.packageName,
                PackageManager.GET_META_DATA
            );
            if (metaSet.metaData != null) {
                val apiKey = metaSet.metaData.getString("public.data.api.key")
                if (apiKey != null) {

                    return apiKey
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {

        }
        return null
    }
}