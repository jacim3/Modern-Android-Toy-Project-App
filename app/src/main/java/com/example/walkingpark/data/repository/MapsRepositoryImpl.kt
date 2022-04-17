package com.example.walkingpark.data.repository

import android.graphics.PointF
import android.util.Log
import com.example.walkingpark.data.mapper.MarkerItemMapper
import com.example.walkingpark.data.source.room.AppDatabase
import com.example.walkingpark.data.source.room.ParkDB
import com.example.walkingpark.domain.model.tools.LatLngPoints
import com.example.walkingpark.domain.model.MarkerItem
import com.example.walkingpark.domain.repository.MapsRepository
import com.google.android.gms.maps.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 *   Google Maps APi 관련 비즈니스 로직 수행
 *
 * */

@Singleton
class MapsRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase
): MapsRepository {


    // DB 쿼리를 수행하기 위한 쿼리를 리턴하는 메서드
    // 1. latLng - 사용자 위경도 값
    // 2. cursorValue - 검색범위 seekBar 값
    // 3. mult - 검색범위가 좁아, 검색결과가 없는 경우, 검색범위를 넓혀서 다시 검색하기 위한 보정값
    override fun getDatabaseQuery(latLng: LatLng, cursorValue:Int, mult:Int): HashMap<String, Double> {
        val latLngPoints = LatLngPoints()

        val center = PointF(latLng.latitude.toFloat(), latLng.longitude.toFloat())
        val adjustValue = mult + cursorValue

        val searchRange = adjustValue * 1000.0     // 검색범위 (M -> KM 변환)
        val p1: PointF = latLngPoints.calculateDerivedPosition(
            center,
            searchRange,
            0.0
        )
        val p2: PointF = latLngPoints.calculateDerivedPosition(
            center,
            searchRange,
            90.0
        )
        val p3: PointF = latLngPoints.calculateDerivedPosition(
            center,
            searchRange,
            180.0
        )
        val p4: PointF = latLngPoints.calculateDerivedPosition(
            center,
            searchRange,
            270.0
        )

        var lat1 = p3.x
        var lat2 = p1.x
        var lng1 = p4.y
        var lng2 = p2.y

        // 범위 설정 문제로 인한 무한루프 방지.
        if (lat1 > lat2) {
            val tmp = lat1
            lat1 = lat2
            lat2 = tmp
        }

        if (lng1 > lng2) {
            val tmp = lng1
            lng1 = lng2
            lng2 = tmp
        }

        val queryMap = HashMap<String,Double>()
        queryMap["startLatitude"] = lat1.toDouble()         // 36
        queryMap["endLatitude"] = lat2.toDouble()           // 38
        queryMap["startLongitude"] = lng1.toDouble()        // 126
        queryMap["endLongitude"] = lng2.toDouble()          // 128
        queryMap["adjustValue"] = adjustValue.toDouble()

        return queryMap
    }

    // DB 의 (A Between B) And (C between D) 쿼리를 수행하여 결과를 받아옴.
    override suspend fun getDatabase(query: Map<String, Double>): HashMap<String, Any> {
        Log.e("query : ", "${query["startLatitude"]} ${query["endLatitude"]} ${query["startLongitude"]} ${query["endLongitude"]} ")

        val response = appDatabase.parkDao().queryRangedDataFromLatLng(
            query["startLatitude"]!!,
            query["endLatitude"]!!,
            query["startLongitude"]!!,
            query["endLongitude"]!!,
        )

        Log.e("query2 : " , response.toString())

        val returnMap = HashMap<String, Any>().apply {
            this["mult"] = query["adjustValue"]!!.toInt()  // 검색 범위 (mult * 1000m) 의 mult 값
            this["response"] = response                    // 보통의 DB 응답 결과
        }


        return returnMap
    }

    // TODO 추후 기능 확장 예정
    // 읽어온 DB 리스트에서 튜플 하나에 대한 데이터를 Marker 데이터로 파싱하기 위한 메서드.
    override fun parsingDatabaseItem(it: ParkDB): MarkerItem {
        return MarkerItemMapper.itemToMarker(it)
    }
}