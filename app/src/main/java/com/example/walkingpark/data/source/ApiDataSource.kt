package com.example.walkingpark.data.source

import com.example.walkingpark.data.model.dto.AirDTO
import com.example.walkingpark.data.model.dto.StationDTO
import com.example.walkingpark.data.model.dto.WeatherDTO
import com.example.walkingpark.data.api.PublicApiService
import com.example.walkingpark.di.module.PublicDataApiModule
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiDataSource @Inject constructor(
    private val apiKey: String,
    @PublicDataApiModule.AirAPI
    private val airApi: PublicApiService,
    @PublicDataApiModule.StationAPI
    private val stationApi: PublicApiService,
    @PublicDataApiModule.WeatherApi
    private val weatherApi: PublicApiService
) {

    fun getWeatherApi(query: Map<String, String>): Flowable<WeatherDTO>? {
        return weatherApi.getWeatherByGridXY(apiKey, query).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAirApi(query: Map<String, String>): Single<AirDTO> {
        return airApi.getAirDataByStationName(apiKey, query).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getStationApi(
        query: Map<String, String>,
    ): Single<StationDTO> {
        return stationApi.getStationDataByName(apiKey, query).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
    }
}