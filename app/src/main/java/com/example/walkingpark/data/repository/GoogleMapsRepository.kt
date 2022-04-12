package com.example.walkingpark.data.repository

import android.graphics.PointF
import android.util.Log
import com.example.walkingpark.components.ui.dialog.LoadingIndicator
import com.example.walkingpark.data.enum.Settings
import com.example.walkingpark.data.room.AppDatabase
import com.example.walkingpark.data.room.ParkDB
import com.example.walkingpark.data.tools.LatLngPoints
import com.example.walkingpark.data.tools.MyItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    var isAllMarkerPrintButtonClicked = false
    var isAllMarkerRemoveButtonClicked = false
    var isReturnToBeginButtonClicked = false
    var isStartWorkoutButtonClicked = false
    var isGoogleMapChasingCamera = true
    var googleMapsZoomLevel: Float = 10f        // Spinner 에 의해 설정되는 구글맵 Zoom 값

    var userGoogleMapZoomLevel: Double = 0.0      // 사용자의 조작에 따른 구글맵 확대/축소값

    private var currentMarker: Marker? = null
    private var currentParkMarkers: MutableList<Marker> = mutableListOf()
    lateinit var loadingIndicator: LoadingIndicator

    lateinit var clusterManager: ClusterManager<ClusterItem>
    var clusteringCheck = true


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

    // DB 에서 쿼리하기 위한 위,경도 검색 범위를 리턴하는 메서드
    private fun changeRangeForReSearch(
        latitude: Double,
        longitude: Double,
        count: Int
    ): Array<Double> {
        val latLngPoints = LatLngPoints()

        val center = PointF(latitude.toFloat(), longitude.toFloat())

        val p1: PointF = latLngPoints.calculateDerivedPosition(
            center,
            userGoogleMapZoomLevel * count,
            0.0
        )
        val p2: PointF = latLngPoints.calculateDerivedPosition(
            center,
            userGoogleMapZoomLevel * count,
            90.0
        )
        val p3: PointF = latLngPoints.calculateDerivedPosition(
            center,
            userGoogleMapZoomLevel * count,
            180.0
        )
        val p4: PointF = latLngPoints.calculateDerivedPosition(
            center,
            userGoogleMapZoomLevel * count,
            270.0
        )

        var lat1 = p3.x
        var lat2 = p1.x
        var lng1 = p4.y
        var lng2 = p2.y

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


        Log.e("MapSearchRange : ", "$lat1 $lat2 $lng1 $lng2")

        return arrayOf(lat1.toDouble(), lat2.toDouble(), lng1.toDouble(), lng2.toDouble())
    }

    // 처리된 결과를 ViewModel 에 전달하기 위하여 수행되며, 지도 관련 비즈니스 로직 작성.
    // TODO 구글맵 로딩이 한번에 되는것이 아닌, fusedLocationClient 의 requestLocationUpdates() 를 통한
    // TODO 콜백으로 위치 정확도가 점점 개선되며 업데이트 하는 방식
    suspend fun getParkDataForMaps(latitude: Double, longitude: Double): List<ParkDB> {

        if (isMapLoaded) {

            if (clusteringCheck) {
                clusteringCheck = false

            }

            userGoogleMapZoomLevel = 21 - googleMap.cameraPosition.zoom * 5000.0    // Zoom 값 초기화
            // 마커 출력 요청에 따라 검색.
            if (isAllMarkerPrintButtonClicked) {

                if (googleMap.cameraPosition.zoom >= 12.0f) {

                } else {
                    isAllMarkerPrintButtonClicked = false
                    googleMap.setOnCameraIdleListener(clusterManager)
                    Log.e("getParkDataForMaps : ", latitude.toString())
                    Log.e("getParkDataForMaps : ", longitude.toString())
                    val result = getParkDataFromRoomDB(latitude, longitude)
                    CoroutineScope(Dispatchers.Default).launch {

                        val markerJob = CoroutineScope(Dispatchers.Main).launch {
                            for (it in result) {
                                setMarkerOptions(it)
                                /*                          val item = googleMap.addMarker(setMarkerOptions(it))
                                                          if (item != null) {
                                                              currentParkMarkers.add(item)
                                                          }*/
                            }
                        }
                        markerJob.join()
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(1000)
                            clusterManager.cluster()
                        }
                        loadingIndicator.dismissIndicator()
                    }
                    return result
                }
            }

            if (isAllMarkerRemoveButtonClicked) {
                isAllMarkerRemoveButtonClicked = false

                //if (currentParkMarkers.size != 0) {

/*                    val job = CoroutineScope(Dispatchers.Main).launch {
                        currentParkMarkers.forEach {
                            clusterManager.clearItems()
                        }
                        //currentParkMarkers.clear()
                    }*/
                clusterManager.removeItems(clusterManager.algorithm.items)
                CoroutineScope(Dispatchers.Main).launch {
                    clusterManager.cluster()
                    loadingIndicator.dismissIndicator()
                }


                //}
            }

            Log.e("UserGoogleMap : ", googleMap.cameraPosition.zoom.toString())
            Log.e("UserGoogleMap : ", googleMap.cameraPosition.tilt.toString())
            Log.e("UserGoogleMap : ", googleMap.cameraPosition.target.toString())
            Log.e("UserGoogleMap : ", googleMap.cameraPosition.bearing.toString())
            if (isStartWorkoutButtonClicked) {

            }

            currentMarker?.remove()         // 기존의 마커를 지워주지 않으면 계속 누적되어 생성됨.


            // TODO 사용자의 위치는 계속 업데이트 해 줄것. -> 지도 정보를 계속 업데이트 하여 정확도를 높이는 방식.
            googleMap.apply {
                currentMarker = addMarker(setUserMarkerOptions(latitude, longitude))
                if (isGoogleMapChasingCamera) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(googleMapsZoomLevel))     // min:2f max:21f
                }
            }
        }
        return emptyList()
    }

    // 사용자 위치 마커 출력 메서드.
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
    private fun setMarkerOptions(it: ParkDB) {
        val markerOptions = MarkerOptions()

        val parkName = it.parkName
        val oldAddress = it.addressDoro
        val newAddress = it.addressJibun
        val phoneNumber = it.phoneNumber
        val category = it.parkCategory
        markerOptions.position(LatLng(it.latitude!!, it.longitude!!));
        if (parkName != null) {
            Log.e("setMarkerOptions() : ", it.parkName)
            markerOptions.title(parkName);
        } else {
            markerOptions.title("????")
        }
        if (category != null) {
            Log.e("setMarkerOptions() : ", it.parkCategory)
            markerOptions.snippet("${it.latitude}   ${it.longitude}");
        } else {
            markerOptions.snippet("???")
        }

        val item = MyItem(it.latitude, it.longitude, "safdasdf", "asfdasdffsad")
        clusterManager.addItem(item)
        // return markerOptions
    }
}