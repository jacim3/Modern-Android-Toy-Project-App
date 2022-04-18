package com.example.walkingpark.data.mapper

import com.example.walkingpark.data.source.room.ParkDB
import com.example.walkingpark.domain.model.MarkerItem

object MarkerItemMapper {

    fun itemToMapper(it: ParkDB) : MarkerItem{
//        val parkAddress = it.addressDoro ?: it.addressJibun
//        val phoneNumber = it.phoneNumber ?: "전화번호 없음"
//        val parkSize = it.parkSize ?: 0.0
//        val parkFacilityCulture = it.facilityCulture ?: "문화시설 없음"
//        val parkFacilityHealth = it.facilityHealth ?: "건강시설 없음"
//        val parkFacilityJoy = it.facilityJoy ?: "오락시설 없음"
//        val parkFacilityUseful = it.facilityUseFul ?: "편의시설 없음"
//        val parkFacilityEtc = it.facilityEtc ?: "그외시설 없음"

        return MarkerItem(
            it.latitude!!,
            it.longitude!!,
            it.parkName ?: "공원이름 없음",
            it.parkCategory ?: "공원",
            it.parkSize!!.toFloat()
        )
    }
}