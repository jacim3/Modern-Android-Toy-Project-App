package com.example.walkingpark.data.repository.datasoruce

import com.example.walkingpark.data.source.api.PublicApiService
import com.example.walkingpark.domain.model.WeatherDTO
import com.example.walkingpark.data.tools.LatLngToGridXy
import com.google.android.gms.maps.model.LatLng
import java.sql.Timestamp

class WeatherApiSource(
    private val apiKey: String,
    private val api: PublicApiService,
    private val latLng: LatLng
) {
    suspend fun fetchData(): List<WeatherDTO.Response.Body.Items.Item>? {
        val query = getQuery(latLng)
        val response = api.getWeatherByGridXY(apiKey, query)
        if (response.isSuccessful) {
            return response.body()?.response?.body?.items?.item
        }
        return null
    }

    private fun getQuery(latLng: LatLng): HashMap<String, String> {

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

        val grid = LatLngToGridXy(latLng.latitude, latLng.longitude)

        val queryMap = HashMap<String, String>().apply {
            this["dataType"] = "json"
            this["base_date"] = date
            this["base_time"] = hourMinute
            this["numOfRows"] = "1000"
            this["nx"] = grid.locX.toString()
            this["ny"] = grid.locX.toString()
        }
        return queryMap
    }
}