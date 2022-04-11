package com.example.walkingpark.di.module

import com.example.walkingpark.data.enum.Common
import com.example.walkingpark.retrofit2.PublicApiService
import com.example.walkingpark.retrofit2.UnsafeOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier


@Module
@InstallIn(SingletonComponent::class)
object PublicDataApiModule {

    // TODO RestApi TimeOut 관련.
    var okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    @AirAPI
    @Provides
    fun provideDataFromAirApi() : PublicApiService {

        val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Common.BASE_URL_API_AIR)
                .addConverterFactory(GsonConverterFactory.create())
                .client(UnsafeOkHttpClient.unsafeOkHttpClient().build())
              //  .client(okHttpClient)
                .build()
        }

        val api: PublicApiService by lazy {
            retrofit.create(PublicApiService::class.java)
        }
        return api
    }

    @StationAPI
    @Provides
    fun provideDataFromStationApi(): PublicApiService {

        val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Common.BASE_URL_API_STATION)
                .addConverterFactory(GsonConverterFactory.create())
                .client(UnsafeOkHttpClient.unsafeOkHttpClient().build())
                //.client(okHttpClient)
                .build()
        }

        val api:PublicApiService by lazy {
            retrofit.create(PublicApiService::class.java)
        }

        return api
    }

    @WeatherApi
    @Provides
    fun provideDataFromWeatherApi(): PublicApiService {

        val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Common.BASE_URL_API_WEATHER)
                .addConverterFactory(GsonConverterFactory.create())
                .client(UnsafeOkHttpClient.unsafeOkHttpClient().build())
           //     .client(okHttpClient)
                .build()
        }

        val api:PublicApiService by lazy {
            retrofit.create(PublicApiService::class.java)
        }

        return api
    }

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class AirAPI

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class StationAPI

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class WeatherApi

}