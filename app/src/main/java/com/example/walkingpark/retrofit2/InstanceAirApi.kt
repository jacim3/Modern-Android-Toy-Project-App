package com.example.walkingpark.retrofit2

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object InstanceAirApi {

    private const val BASE_URL = "https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/"
    private val retrofit:Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(UnsafeOkHttpClient.unsafeOkHttpClient().build())
            .build()
    }

    val api:PublicApiController by lazy {
        retrofit.create(PublicApiController::class.java)
    }
}