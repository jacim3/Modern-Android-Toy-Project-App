package com.example.walkingpark.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.example.walkingpark.constants.Common
import com.example.walkingpark.data.source.api.PublicApiService
import com.example.walkingpark.domain.model.tools.LatLngToGridXy
import com.example.walkingpark.di.module.PublicDataApiModule
import com.example.walkingpark.domain.repository.WeatherApiRepository
import com.example.walkingpark.data.source.api.dto.WeatherDTO
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response
import java.lang.Exception
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashMap


@Singleton
class WeatherApiRepositoryImpl @Inject constructor(
    private val apiKey: String,
    @PublicDataApiModule.WeatherApi
    private val weatherApi: PublicApiService
) : WeatherApiRepository {

    override suspend fun startWeatherApi(query: Map<String, String>): Response<WeatherDTO> {
        return weatherApi.getWeatherByGridXY(apiKey, query)
    }

    // TODO 공공데이터포털은 서버에 에러가 생기지 않는 이상, 데이터를 받아오게 되면 resultCode : 200 을 반환한다.

    override fun getQuery(timeMap: Map<String, String>, grid: LatLngToGridXy): Map<String, String> {

        val queryMap = HashMap<String, String>().apply {
            this["dataType"] = "json"
            this["base_date"] = timeMap["date"].toString()
            this["base_time"] = timeMap["time"].toString()
            this["numOfRows"] = "1000"
            this["nx"] = grid.locX.toString()
            this["ny"] = grid.locY.toString()
        }

        return queryMap
    }

    // TODO 시간계산을 위해 Java Calendar() 및 SimpleDateFormat() 사용
    // Weather Api 는 1시간 단위로, 현재 시각에서 1시간 전 까지의 데이터를 요청 가능. 또한 AM 02:00 부터 데이터 요청 가능
    @SuppressLint("SimpleDateFormat")
    override fun getTimeForQuery(): Map<String, String> {

        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(Common.REST_API_DATE_UNIT_FORMAT)
        val timeFormat = SimpleDateFormat(Common.REST_API_TIME_UNIT_FORMAT)

        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.HOUR, -1)
        val hour = cal.get(Calendar.HOUR)
        if (hour < 2) {
            cal.set(Calendar.MINUTE, 23)
            cal.add(Calendar.DATE, -1)
        }

        val date = dateFormat.format(cal.time)
        val time = timeFormat.format(cal.time)

        val timeMap = HashMap<String, String>()
        timeMap.apply {
            this["date"] = date
            this["time"] = time
        }
        return timeMap
    }

    override fun changeLatLngToGrid(latLng: LatLng): LatLngToGridXy {
        return LatLngToGridXy(latLng.latitude, latLng.longitude)
    }

    override fun handleResponse(response: Response<WeatherDTO>) : List<WeatherDTO.Response.Body.Items.Item>? {

        try {

            Log.e("response : ", response.toString())
            return response.body()?.response?.body?.items?.item
        } catch (e: Exception) { }
        return null
    }
}