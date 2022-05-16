package com.example.walkingpark.data.source

import com.example.walkingpark.data.model.dto.AirResponse
import com.example.walkingpark.data.model.dto.StationResponse
import com.example.walkingpark.data.api.PublicApiService
import com.example.walkingpark.data.model.dto.WeatherResponse
import com.example.walkingpark.di.module.PublicDataApiModule
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

    fun provideApiKey() = apiKey

    fun provideWeatherService(): PublicApiService = weatherApi

    fun getWeatherApi(query:Map<String, String>): Single<WeatherResponse> {
        return weatherApi.getWeatherByGridXY(apiKey, query).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAirApi(query: Map<String, String>): Single<AirResponse> {
        return airApi.getAirDataByStationName(apiKey, query).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getStationApi(
        query: Map<String, String>,
    ): Single<StationResponse> {
        return stationApi.getStationDataByName(apiKey, query).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
    }
}