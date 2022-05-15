package com.example.walkingpark.data.api

import androidx.paging.PagingSource
import com.example.walkingpark.constants.Common
import com.example.walkingpark.data.model.dto.AirDTO
import com.example.walkingpark.data.model.dto.StationDTO
import com.example.walkingpark.data.model.dto.WeatherDTO
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface PublicApiService {

    @GET(Common.REQUEST_PATH_AIR_API)
    fun getAirDataByStationName(
        @Query(value= "serviceKey", encoded = true) serviceKey:String,
        @QueryMap querySet:Map<String, String>
    ):Single<AirDTO>

    @GET(Common.REQUEST_PATH_STATION_API)
   fun getStationDataByName(
        @Query(value = "serviceKey", encoded = true) serviceKey:String,
        @QueryMap querySet: Map<String, String>
    ) : Single<StationDTO>

    // TODO 동네예보 조회 Api : HttpException: resultCode 500 발생 !!
    @GET(Common.REQUEST_PATH_WEATHER_API)
  fun getWeatherByGridXY(
        @Query(value = "serviceKey", encoded = true) serviceKey: String,
        @QueryMap querySet: Map<String, String>
    ) : Single<PagingSource.LoadResult<String,WeatherDTO>>
}