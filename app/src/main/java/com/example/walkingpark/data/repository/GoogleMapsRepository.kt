package com.example.walkingpark.data.repository

import android.graphics.PointF
import android.util.Log
import com.example.walkingpark.data.enum.Settings
import com.example.walkingpark.data.room.AppDatabase
import com.example.walkingpark.data.room.ParkDB
import com.example.walkingpark.data.tools.LatLngPoints
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

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

    var isAllMarkerPrintButtonClicked = false
    var isAllMarkerRemoveButtonClicked = false
    var isStartWorkoutButtonClicked = false
    var isGoogleMapChasingCamera = true
    var googleMapsZoomLevel: Float = 10f

    private var currentMarker: Marker? = null
    private var currentParkMarkers: MutableList<Marker> = mutableListOf()

    // 사용자의 위경도 위치를 기준으로 근처 아이템을 찾을때까지 범위를 확장한 between 쿼리 반복을 통하여 Room 에서
    // 데이터를 가져온다. 가져온 데이터는 GoogleMaps 비즈니스 로직 수행을 위해 MapsRepository 로 전달.
    // TODO 현재는 사용자기준 대략 1km 범위를 검색.
    private suspend fun getParkDataFromRoomDB(latitude: Double, longitude: Double): List<ParkDB> {

        var receivedData = emptyList<ParkDB>()
        var count = 1
        while (receivedData.isEmpty()) {
            val rangeArray = changeRangeForReSearch(latitude, longitude, count)
            receivedData = appDatabase.parkDao().queryRangedDataFromLatLng(
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
        val latLngPoints = LatLngPoints()

        val center = PointF(latitude.toFloat(), longitude.toFloat())

        val p1: PointF = latLngPoints.calculateDerivedPosition(
            center,
            Settings.LOCATION_SEARCH_RADIUS * count,
            0.0
        )
        val p2: PointF = latLngPoints.calculateDerivedPosition(
            center,
            Settings.LOCATION_SEARCH_RADIUS * count,
            90.0
        )
        val p3: PointF = latLngPoints.calculateDerivedPosition(
            center,
            Settings.LOCATION_SEARCH_RADIUS * count,
            180.0
        )
        val p4: PointF = latLngPoints.calculateDerivedPosition(
            center,
            Settings.LOCATION_SEARCH_RADIUS * count,
            270.0
        )

        val lat1 = p3.x
        val lat2 = p1.x
        val lng1 = p4.y
        val lng2 = p2.y

        return arrayOf(lat1.toDouble(), lat2.toDouble(), lng1.toDouble(), lng2.toDouble())
    }

    // 처리된 결과를 ViewModel 에 전달하기 위하여 수행되며, 지도 관련 비즈니스 로직 작성.
    // TODO 구글맵 로딩이 한번에 되는것이 아닌, fusedLocationClient 의 requestLocationUpdates() 를 통한
    // TODO 콜백으로 위치 정확도가 점점 개선되며 업데이트 하는 방식
    suspend fun getParkDataForMaps(latitude: Double, longitude: Double): List<ParkDB> {

        if (isMapLoaded) {
            if (isAllMarkerPrintButtonClicked) {
                isAllMarkerPrintButtonClicked = false

                // isRefreshRequested = false
                Log.e("getParkDataForMaps : ", latitude.toString())
                Log.e("getParkDataForMaps : ", longitude.toString())
                val result = getParkDataFromRoomDB(latitude, longitude)
                CoroutineScope(Dispatchers.Main).launch {
                    for (it in result) {

                        val item = googleMap.addMarker(setMarkerOptions(it))
                        if (item != null) {
                            currentParkMarkers.add(item)
                        }
                    }
                }
                return result
            }

            if (isAllMarkerRemoveButtonClicked) {
                isAllMarkerRemoveButtonClicked = false

                if (currentParkMarkers.size != 0) {
                    currentParkMarkers.forEach {
                        it.remove()
                    }
                    currentParkMarkers.clear()
                }
            }

            if (isStartWorkoutButtonClicked) {

            }

            currentMarker?.remove()         // 기존의 마커를 지워주지 않으면 계속 누적되어 생성됨.


            // TODO 사용자의 위치는 계속 업데이트 해 줄것. -> 지도 정보를 계속 업데이트 하여 정확도를 높이는 방식.
            googleMap.apply {
                currentMarker = addMarker(setUserMarkerOptions(latitude, longitude))
                if (isGoogleMapChasingCamera) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
                }
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(googleMapsZoomLevel))     // min:2f max:21f
            }
        }
        return emptyList()
    }

    // 사용자 마커 생성 메서드.
    private fun setUserMarkerOptions(latitude: Double, longitude: Double): MarkerOptions {
        val markerOptions = MarkerOptions()
        markerOptions.position(LatLng(latitude, longitude))
        markerOptions.title("내 위치")
        markerOptions.snippet("TODO 주소")
        markerOptions.draggable(true)
        // markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        return markerOptions
    }

    // param 1 : List<ParkDB> 의 객체값중 하나를 받아와 MarkerOptions 객체를 만들어 리턴.
    private fun setMarkerOptions(it: ParkDB): MarkerOptions {
        val markerOptions = MarkerOptions()

        val parkName = it.parkName
        val oldAddress = it.addressDoro
        val newAddress = it.addressJibun
        val phoneNumber = it.phoneNumber
        val category = it.parkCategory
        markerOptions.position(LatLng(it.latitude!!, it.longitude!!));

        if (parkName != null) {
            markerOptions.title(parkName);
        } else {
            markerOptions.title("????")
        }
        if (category != null) {
            markerOptions.snippet(it.parkCategory);
        } else {
            markerOptions.snippet("???")
        }

        markerOptions.draggable(true)
        return markerOptions
    }


}