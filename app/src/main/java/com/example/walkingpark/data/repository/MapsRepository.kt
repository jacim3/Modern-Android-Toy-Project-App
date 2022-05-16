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
        getDatabaseQuery(entity, cursorValue, mult)
            .apply {
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
        return Array(4) { i -> i * 90.0 }.map {
            LatLngPoints().calculateDerivedPosition(
                PointF(entity.latitude.toFloat(), entity.longitude.toFloat()),
                mult + cursorValue * 1000.0,
                it
            ).run {
                if (it == 0.0 || it == 180.0) this.x else this.y
            }
        }.toList().sorted().run {
            LocationSearchEntity(
                startLatitude = this[0].toDouble(),
                endLatitude = this[1].toDouble(),
                startLongitude = this[2].toDouble(),
                endLongitude = this[3].toDouble(),
                adjustValue = (mult + cursorValue).toDouble()
            )
        }
    }


    // 읽어온 DB 리스트에서 튜플 하나에 대한 데이터를 Marker 데이터로 파싱하기 위한 메서드.
    fun parsingDatabaseItem(it: ParkDB): MarkerItem {
        return MarkerItemMapper.itemToMapper(it)
    }

}