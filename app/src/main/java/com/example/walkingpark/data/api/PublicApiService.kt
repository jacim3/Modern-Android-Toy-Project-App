package com.example.walkingpark.data.api

import com.example.walkingpark.constants.Common
import com.example.walkingpark.data.model.dto.AirResponse
import com.example.walkingpark.data.model.dto.StationResponse
import com.example.walkingpark.data.model.dto.WeatherResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface PublicApiService {

    @GET(Common.REQUEST_PATH_AIR_API)
    fun getAirDataByStationName(
        @Query(value= "serviceKey", encoded = true) serviceKey:String,
        @QueryMap querySet:Map<String, String>
    ):Single<AirResponse>

    @GET(Common.REQUEST_PATH_STATION_API)
   fun getStationDataByName(
        @Query(value = "serviceKey", encoded = true) serviceKey:String,
        @QueryMap querySet: Map<String, String>
    ) : Single<StationResponse>

    // TODO 동네예보 조회 Api : HttpException: resultCode 500 발생 !!
    @GET(Common.REQUEST_PATH_WEATHER_API)
  fun getWeatherByGridXY(
        @Query(value = "serviceKey", encoded = true) serviceKey: String,
        @QueryMap querySet: Map<String, String>
    ) : Single<WeatherResponse>
}