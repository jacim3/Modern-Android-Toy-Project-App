package com.example.walkingpark.data.source.api

import com.example.walkingpark.data.source.api.dto.AirDTO
import com.example.walkingpark.data.source.api.dto.StationDTO
import com.example.walkingpark.data.source.api.dto.WeatherDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface PublicApiService {

    @GET("getMsrstnAcctoRltmMesureDnsty")
    suspend fun getAirDataByStationName(
        @Query(value= "serviceKey", encoded = true) serviceKey:String,
        @QueryMap querySet:Map<String, String>
/*        @Query("returnType") type:String,
        @Query("stationName") stationName:String,
        @Query("dataTerm") dataTerm:String*/
    ):Response<AirDTO>

    @GET("getMsrstnList")
    suspend fun getStationDataByName(
        @Query(value = "serviceKey", encoded = true) serviceKey:String,
        @QueryMap querySet: Map<String, String>

/*        @Query("returnType") type:String,
        @Query("addr") addr:String,*/
    ) : Response<StationDTO>


    // TODO 동네예보 조회 Api : HttpException: resultCode 500 발생 !!
    @GET("getUltraSrtFcst")
    suspend fun getWeatherByGridXY(
        @Query(value = "serviceKey", encoded = true) serviceKey: String,
        @QueryMap querySet: Map<String, String>
/*        @Query("dataType") type:String,
        @Query("base_date") baseDate:String,       // yyyymmdd
        @Query("base_time") baseTime:String,       // hhmm : 30분 단위
        @Query("numOfRows") numOfRows:String,
        @Query("nx") gridX:String,
        @Query("ny") gridY:String*/
    ) :  Response<WeatherDTO>
}