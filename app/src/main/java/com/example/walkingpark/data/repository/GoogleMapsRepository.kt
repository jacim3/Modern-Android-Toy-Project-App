package com.example.walkingpark.data.repository

import android.util.Log
import com.example.walkingpark.data.enum.Logic
import com.example.walkingpark.data.room.AppDatabase
import com.example.walkingpark.data.room.ParkDB
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import javax.inject.Inject
import javax.inject.Singleton

/**
 *   Google Maps APi 관련 비즈니스 로직 수행
 *
 * */

@Singleton
class GoogleMapsRepository @Inject constructor() {

    @Inject
    lateinit var appDatabase: AppDatabase

    lateinit var googleMap: GoogleMap // onMapReady() 로 부터 받아오게 될 GoogleMap 객체

    var isMapLoaded = false          // onMapReady() 호출 후 google Map 객체가 초기화되므로 이를 확인하기 위함
    var isRefreshRequested = true    // 맵 마커 refresh 요청 체크. 기존의 마커를 모두 지우고 다시 생성해야 함.
    var isDataBaseLoaded = false


    // 사용자의 위경도 위치를 기준으로 근처 아이템을 찾을때까지 범위를 확장한 between 쿼리 반복을 통하여 Room 에서
    // 데이터를 가져온다. 가져온 데이터는 GoogleMaps 비즈니스 로직 수행을 위해 MapsRepository 로 전달.
    // TODO 현재는 사용자기준 대략 1km 범위를 검색.
    private suspend fun getParkDataFromRoomDB(latitude: Double, longitude: Double): List<ParkDB> {

        var receivedData = emptyList<ParkDB>()
        var count = 1
        while (receivedData.isEmpty()) {
            val rangeArray = changeRangeForReSearch(latitude, longitude, count)
            receivedData = appDatabase.parkDao().queryRangedDataFromLatLng(
                rangeArray[0],
                rangeArray[1],
                rangeArray[2],
                rangeArray[3]
            )
            count++
        }
        return receivedData
    }

    // 위 로직의 getParkDataFromRoomDB() 의 검색범위를 넓히는 메서드.
    private fun changeRangeForReSearch(
        latitude: Double,
        longitude: Double,
        count: Int
    ): Array<Double> {
        val lat1 = latitude - (Logic.SEARCH_LAT_AREA * count)
        val lat2 = latitude + (Logic.SEARCH_LAT_AREA * count)
        val lng1 = longitude - (Logic.SEARCH_LNG_AREA * count)
        val lng2 = longitude + (Logic.SEARCH_LNG_AREA * count)

        return arrayOf(lat1, lat2, lng1, lng2)
    }

    // 처리된 결과를 ViewModel 에 전달하기 위하여 수행되며, 지도 관련 비즈니스 로직 작성.
    // TODO 구글맵 로딩이 한번에 되는것이 아닌, fusedLocationClient 의 requestLocationUpdates() 를 통한
    // TODO 콜백으로 위치 정확도가 점점 개선되며 업데이트 하는 방식
    suspend fun getParkDataForMaps(latitude: Double, longitude: Double): List<ParkDB> {

        if (isMapLoaded) {
            if (!isDataBaseLoaded) {
                isDataBaseLoaded = true

                // isRefreshRequested = false

                val result = getParkDataFromRoomDB(latitude, longitude)
                Log.e("11111111111111", result.size.toString())
                Log.e("22222222222222", isRefreshRequested.toString())

                result.forEach {
                    Log.e("responseList : ", "${it.latitude}  ${it.longitude}")
                }


                return result

            }

            /*                val markerOptions = MarkerOptions()
                markerOptions.position(seoul);
                markerOptions.title("서울");
                markerOptions.snippet("수도");

                googleMap.addMarker(markerOptions);*/
            val seoul = LatLng(latitude, longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(seoul))
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(17f))        // min:2f max:21f
        }
        return emptyList()
    }
}