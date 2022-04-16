package com.example.walkingpark.data.repository

import android.graphics.PointF
import com.example.walkingpark.data.source.room.AppDatabase
import com.example.walkingpark.data.source.room.ParkDB
import com.example.walkingpark.data.tools.LatLngPoints
import com.example.walkingpark.domain.model.MarkerItem
import com.example.walkingpark.domain.MapsRepository
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

    //// TODO REPOSITORY
    // 사용자의 위경도 위치를 기준으로 근처 아이템을 찾을때까지 범위를 확장한 between 쿼리 반복을 통하여 Room 에서
    // 데이터를 가져온다. 가져온 데이터는 GoogleMaps 비즈니스 로직 수행을 위해 MapsRepository 로 전달.
    // TODO 현재는 SeekBar 를 통하여 출력되는 Circle 범위의 위치를 모두 검색하고, 설정범위 내에서 찾아내지 못할 경우,
    // TODO 범위를 확대하여 검색함. 이 결과는 다시 SeekBar 에 전달.
    // TODO 위경도 계산에는 복잡한 수학공식이 이용되므로, TODO 외부 클래스를 가져와 이를 통하여 계산하여 사용.
    override suspend fun getDatabase(query: Map<String, Double>): HashMap<String, Any> {
        val response = appDatabase.parkDao().queryRangedDataFromLatLng(
            query["startLatitude"]!!,
            query["endLatitude"]!!,
            query["startLongitude"]!!,
            query["endLongitude"]!!,
        )

        val returnMap = HashMap<String, Any>()
        returnMap["mult"] = query["adjustValue"]!!.toInt()
        returnMap["response"] = response

        return returnMap
    }

    override fun getDatabaseQuery(latLng: LatLng, cursorValue:Int, mult:Int): HashMap<String, Double> {

        val latLngPoints = LatLngPoints()

        val center = PointF(latLng.latitude.toFloat(), latLng.longitude.toFloat())
        val adjustValue = mult + cursorValue

        val searchRange = mult * cursorValue * 1000.0      // 검색범위 (M -> KM 변환)
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
        queryMap["startLatitude"] = lat1.toDouble()
        queryMap["endLatitude"] = lat2.toDouble()
        queryMap["startLongitude"] = lng1.toDouble()
        queryMap["endLongitude"] = lat2.toDouble()
        queryMap["adjustValue"] = adjustValue.toDouble()

        return queryMap
    }

    // TODO USECASE
    // TODO 마커클러스터 커스터마이징 필요. -> 현재는 HashMap 을 통하여 개별 마커에 대한 정보를 저장.
    // param 1 : List<ParkDB> 의 객체값중 하나를 받아와 item 을 생성해서 clusterManager 에 등록하여 마커 저장
    override fun parsingDatabaseItem(it: ParkDB): MarkerItem {
        //  val markerOptions = MarkerOptions()

        //    markerOptions.position(LatLng(it.latitude!!, it.longitude!!));

        val parkName = it.parkName ?: "공원이름 없음"
        val parkAddress = it.addressDoro ?: it.addressJibun
        val phoneNumber = it.phoneNumber ?: "전화번호 없음"
        val parkSize = it.parkSize ?: 0.0
        val parkCategory = it.parkCategory ?: "공원"
        val parkFacilityCulture = it.facilityCulture ?: "문화시설 없음"
        val parkFacilityHealth = it.facilityHealth ?: "건강시설 없음"
        val parkFacilityJoy = it.facilityJoy ?: "오락시설 없음"
        val parkFacilityUseful = it.facilityUseFul ?: "편의시설 없음"
        val parkFacilityEtc = it.facilityEtc ?: "그외시설 없음"

/*        if (category != null) {
            Log.e("setMarkerOptions() : ", it.parkCategory)
            markerOptions.snippet("${it.latitude}   ${it.longitude}");
        } else {
            markerOptions.snippet("???")
        }*/

        // 위도경도는 DB 에서 Null 값을 걸러냈으므로 Null 값이 들어가지 않음.

        return MarkerItem(
            it.latitude!!,
            it.longitude!!,
            parkName,
            parkCategory,
            it.parkSize!!.toFloat()
        )
        // return markerOptions
    }
}