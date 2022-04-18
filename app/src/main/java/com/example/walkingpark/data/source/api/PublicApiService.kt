package com.example.walkingpark.data.source.api

import com.example.walkingpark.constants.Common
import com.example.walkingpark.domain.model.AirDTO
import com.example.walkingpark.domain.model.StationDTO
import com.example.walkingpark.domain.model.WeatherDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface PublicApiService {

    @GET(Common.REQUEST_PATH_AIR_API)
    suspend fun getAirDataByStationName(
        @Query(value= "serviceKey", encoded = true) serviceKey:String,
        @QueryMap querySet:Map<String, String>
    ):Response<AirDTO>

    @GET(Common.REQUEST_PATH_STATION_API)
    suspend fun getStationDataByName(
        @Query(value = "serviceKey", encoded = true) serviceKey:String,
        @QueryMap querySet: Map<String, String>
    ) : Response<StationDTO>

    // TODO 동네예보 조회 Api : HttpException: resultCode 500 발생 !!
    @GET(Common.REQUEST_PATH_WEATHER_API)
    suspend fun getWeatherByGridXY(
        @Query(value = "serviceKey", encoded = true) serviceKey: String,
        @QueryMap querySet: Map<String, String>
    ) :  Response<WeatherDTO>
}