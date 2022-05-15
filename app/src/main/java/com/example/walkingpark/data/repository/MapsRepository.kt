package com.example.walkingpark.data.repository

import android.graphics.PointF
import com.example.walkingpark.data.model.mapper.MarkerItemMapper
import com.example.walkingpark.data.room.ParkDB
import com.example.walkingpark.data.tools.LatLngPoints
import com.example.walkingpark.data.model.MarkerItem
import com.example.walkingpark.data.model.entity.LocationEntity
import com.example.walkingpark.data.model.entity.LocationSearchEntity
import com.example.walkingpark.data.source.RoomDataSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 *   Google Maps APi 관련 비즈니스 로직 수행
 *
 * */

@Singleton
class MapsRepository @Inject constructor(
    private val roomDataSource: RoomDataSource
) {

    private var seekBarMult = 0.0
    fun searchLocation(entity: LocationEntity, cursorValue: Int, mult: Int) =
        getDatabaseQuery(entity, cursorValue, mult).apply {
            seekBarMult = this.adjustValue
        }.run {
            roomDataSource.searchDatabase(this)
        }

    fun getSeekBarMult() = seekBarMult


    // DB 에서 데이터를 뽑아내기 위한 쿼리를 리턴하는 메서드
    // 1. latLng - 사용자 위경도 값
    // 2. cursorValue - 검색범위 seekBar 값
    // 3. mult - 검색범위가 좁아, 검색결과가 없는 경우, 검색범위를 넓혀서 다시 검색하기 위한 보정값
    private fun getDatabaseQuery(
        entity: LocationEntity,
        cursorValue: Int,
        mult: Int
    ): LocationSearchEntity {
        val latLngPoints = LatLngPoints()

        val center = PointF(entity.latitude.toFloat(), entity.longitude.toFloat())
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

        return setDataToEntity(lat1, lat2, lng1, lng2, adjustValue)
    }

    private fun setDataToEntity(
        lat1: Float,
        lat2: Float,
        lng1: Float,
        lng2: Float,
        adjustValue: Int
    ): LocationSearchEntity {
        return LocationSearchEntity(
            startLatitude = lat1.toDouble(),
            endLatitude = lat2.toDouble(),
            startLongitude = lng1.toDouble(),
            endLongitude = lng2.toDouble(),
            adjustValue = adjustValue.toDouble()
        )
    }

    // 읽어온 DB 리스트에서 튜플 하나에 대한 데이터를 Marker 데이터로 파싱하기 위한 메서드.
    fun parsingDatabaseItem(it: ParkDB): MarkerItem {
        return MarkerItemMapper.itemToMapper(it)
    }

}