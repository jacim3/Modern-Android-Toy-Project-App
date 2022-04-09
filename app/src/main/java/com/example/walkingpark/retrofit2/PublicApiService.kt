package com.example.walkingpark.retrofit2

import com.example.walkingpark.dto.AirDTO
import com.example.walkingpark.dto.StationNameDTO
import com.example.walkingpark.dto.StationTmDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PublicApiService {


    @GET("")
    suspend fun getAirDataByStationName(
        @Query(value= "serviceKey", encoded = true) serviceKey:String,
        @Query("returnType") type:String,
        @Query("stationName") stationName:String
    ):Response<AirDTO>

    @GET("getTMStdrCrdnt")
    suspend fun getStationDataByName(
        @Query(value = "serviceKey", encoded = true) serviceKey:String,
        @Query("returnType") type:String,
        @Query("umdName") name:String
    ) : Response<StationNameDTO>

    @GET("getNearbyMsrstnList")
    suspend fun getStationDataByTmxTmy(
        @Query(value = "serviceKey", encoded = true) serviceKey:String,
        @Query("returnType") type:String,
        @Query("tmX") tmX:String,
        @Query("tmY") tmY:String
    ):Response<StationTmDTO>
}