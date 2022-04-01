package com.example.walkingpark.retrofit2

import com.example.walkingpark.dto.AirDTO
import com.example.walkingpark.dto.ParkDTO
import com.example.walkingpark.dto.StationNameDTO
import com.example.walkingpark.dto.StationTmDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PublicApiController {


    @GET("")
    suspend fun getAirDataByStationName(
        @Query(value= "serviceKey", encoded = true) serviceKey:String,
        @Query("returnType") type:String,
        @Query("stationName") stationName:String
    ):Response<AirDTO>

    @GET("tn_pubr_public_cty_park_info_api")
    suspend fun getParkData(
        // 인코딩 여부 반드시 체크할것!! apiKey 의 경우 인코딩 작업이 추가로 이루어지지 않게 true 로 설정해야 함.
        @Query(value= "serviceKey", encoded = true) serviceKey:String,
        @Query("pageNo") pageNo:String,
        @Query("type") type:String
    ): Response<ParkDTO>


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