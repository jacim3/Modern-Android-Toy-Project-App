package com.example.walkingpark.data.repository

import com.example.walkingpark.data.source.ApiDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AirApiRepository @Inject constructor(
    private val apiDataSource: ApiDataSource
){

    fun startAirApi(stationName:String) = apiDataSource.getAirApi(getAirQuery(stationName))

    // 버전을 포함하지 않고 호출할 경우 : PM2.5 데이터가 포함되지 않은 원래 오퍼레이션 결과 표출.
    // 버전 1.0을 호출할 경우 : PM2.5 데이터가 포함된 결과 표출.
    // 버전 1.1을 호출할 경우 : PM10, PM2.5 24시간 예측이동 평균데이터가 포함된 결과 표출.
    // 버전 1.2을 호출할 경우 : 측정망 정보 데이터가 포함된 결과 표출.
    // 버전 1.3을 호출할 경우 : PM10, PM2.5 1시간 등급 자료가 포함된 결과 표출
    private fun getAirQuery(stationName: String) = mapOf(
        "returnType" to "json",
        "stationName" to stationName,
        "dataTerm" to "daily",
        "ver" to "1.3"
    )
}