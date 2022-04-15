package com.example.walkingpark.data.repository

import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.util.Log
import com.example.walkingpark.presentation.view.LoadingIndicator
import com.example.walkingpark.data.enum.Settings
import com.example.walkingpark.data.room.AppDatabase
import com.example.walkingpark.data.room.ParkDB
import com.example.walkingpark.data.tools.LatLngPoints
import com.example.walkingpark.data.tools.MyItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.PI
import kotlin.math.sqrt

/**
 *   Google Maps APi 관련 비즈니스 로직 수행
 *
 * */

@Singleton
class GoogleMapsRepository @Inject constructor(@ApplicationContext context: Context) {

    @Inject
    lateinit var appDatabase: AppDatabase

    lateinit var googleMap: GoogleMap // onMapReady() 로 부터 받아오게 될 GoogleMap 객체

    var isMapLoadCompleted = false          // onMapReady() 호출 후 google Map 객체가 초기화되므로 이를 확인하기 위함
    var isRefreshRequested = true    // 맵 마커 refresh 요청 체크. 기존의 마커를 모두 지우고 다시 생성해야 함.

    var isStartWorkoutButtonClicked = false
    var isClickedMarkerPrintButton = false
    var isClickedMarkerRemoveButton = false
    var isClickedLocationReturnButton = false
    var isSelectedZoomInMenuSpinner = false

    var googleMapsZoomLevel: Float = 10f        // Spinner 에 의해 설정되는 구글맵 Zoom 값

    var userGoogleMapZoomLevel: Double = 0.0      // 사용자의 조작에 따른 구글맵 확대/축소값
    var getSearchRangeFromSeekBar = 3
    var parkNameSizeMap = HashMap<String, Double>()
    var parkNameLatLngMap = HashMap<String, String>()

    private var currentMarker: Marker? = null
    var loadingIndicator = LoadingIndicator(context, "asfd")

    lateinit var clusterManager: ClusterManager<ClusterItem>

    private var markerCircleUserLocation: Circle? = null
    private var markerCircleClusterItem: Circle? = null

    private var counter = 0             // 현재 콜백에 따라 로직이 몇번이나 수행되었는지 체크하기 위한 변수.


    // ParkMapsFragment 의 onMapReady() 에서 수행되는 로직
    fun onMapReady(){
        googleMap.setOnCameraIdleListener(clusterManager)

        // MarkerCluster 에서 클러스터 하나하나를 이루는 아이템 (마커) 의 클릭 이벤트.
        clusterManager.setOnClusterItemClickListener {
            if (markerCircleClusterItem != null) markerCircleClusterItem!!.remove()

            // 각각의 마커정보에 대하여 DB 에서 제공하는 면적 정보를 가져와, 반지름으로 변환.
            val radius = sqrt(parkNameSizeMap[it.title]!! / PI)
            markerCircleClusterItem = googleMap.addCircle(CircleOptions().apply {
                val latLng = parkNameLatLngMap[it.title]!!.split(" ")
                center(LatLng(latLng[0].toDouble(), latLng[1].toDouble()))
                radius(radius)
                strokeColor(Color.YELLOW)
            })
            false
        }

        // 클러스터를 눌렀을 때 수행할 이벤트값.
        clusterManager.setOnClusterClickListener {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.position.latitude, it.position.longitude)))
/*            googleMap.animateCamera(CameraUpdateFactory.zoomTo(Settings.GOOGLE_MAPS__ZOOM_LEVEL_HIGH))
            clusterManager.clusterMarkerCollection.markers.*/
            false
        }
    }

    // 사용자의 위경도 위치를 기준으로 근처 아이템을 찾을때까지 범위를 확장한 between 쿼리 반복을 통하여 Room 에서
    // 데이터를 가져온다. 가져온 데이터는 GoogleMaps 비즈니스 로직 수행을 위해 MapsRepository 로 전달.
    // TODO 현재는 SeekBar 를 통하여 출력되는 Circle 범위의 위치를 모두 검색하고, 설정범위 내에서 찾아내지 못할 경우,
    // TODO 범위를 확대하여 검색함. 이 결과는 다시 SeekBar 에 전달.
    // TODO 위경도 계산에는 복잡한 수학공식이 이용되므로, TODO 외부 클래스를 가져와 이를 통하여 계산하여 사용.
    private suspend fun getParkDataFromRoomDB(latitude: Double, longitude: Double): List<ParkDB> {

        var receivedData = emptyList<ParkDB>()
        var count = 0
        while (receivedData.isEmpty()) {
            val rangeArray = changeRangeForReSearch(latitude, longitude, count)
            receivedData = appDatabase.parkDao().queryRangedDataFromLatLng(
                rangeArray[0],
                rangeArray[1],
                rangeArray[2],
                rangeArray[3]
            )
            count++
            getSearchRangeFromSeekBar = rangeArray[4].toInt()
        }
        CoroutineScope(Dispatchers.Main).launch {
            if (markerCircleUserLocation != null) {
                markerCircleUserLocation!!.remove()
            }
            addCircleToMarker("user", latitude, longitude, getSearchRangeFromSeekBar*1000.0)
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
        val adjustValue = count + getSearchRangeFromSeekBar

        userGoogleMapZoomLevel = count * getSearchRangeFromSeekBar * 1000.0
        val p1: PointF = latLngPoints.calculateDerivedPosition(
            center,
            userGoogleMapZoomLevel,
            0.0
        )
        val p2: PointF = latLngPoints.calculateDerivedPosition(
            center,
            userGoogleMapZoomLevel,
            90.0
        )
        val p3: PointF = latLngPoints.calculateDerivedPosition(
            center,
            userGoogleMapZoomLevel,
            180.0
        )
        val p4: PointF = latLngPoints.calculateDerivedPosition(
            center,
            userGoogleMapZoomLevel,
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

        return arrayOf(lat1.toDouble(), lat2.toDouble(), lng1.toDouble(), lng2.toDouble(), adjustValue.toDouble())
    }

    // 처리된 결과를 ViewModel 에 전달하기 위하여 수행되며, 지도 관련 비즈니스 로직 작성.
    // TODO 위치 업데이트 콜백이 발생할때마다 업데이트 되는 LiveDataHolder 를 Observe 하여 ViewModel 에서 이를
    // TODO 체크하여 이벤트 트리거가 발생한다. ParkMapsFragment 의 onMapReady() 가 완료됨에 따라 구글맵과
    // TODO 위치 업데이트 관련 비즈니스 로직을 콜백함수에 맞추어 여기에 작성.
    suspend fun getParkDataForMaps(latitude: Double, longitude: Double): List<ParkDB> {

        if (isMapLoadCompleted) {

            if (loadingIndicator.flag !== "None") {
                loadingIndicator.flag = "None"
                loadingIndicator.dismissIndicator()
            }

            counter ++
            CoroutineScope(Dispatchers.Main).launch {
                if (markerCircleUserLocation != null) {
                    markerCircleUserLocation!!.remove()
                }
                addCircleToMarker("user", latitude, longitude, getSearchRangeFromSeekBar*1000.0)
            }

            // TODO 1. 공원 검색 (마커 출력) 버튼이 눌렸을 때 수행할 로직 정의
            if (isClickedMarkerPrintButton) {

                isClickedMarkerPrintButton = false
                googleMap.setOnCameraIdleListener(clusterManager)
                Log.e("getParkDataForMaps : ", latitude.toString())
                Log.e("getParkDataForMaps : ", longitude.toString())
                val result = getParkDataFromRoomDB(latitude, longitude)
                CoroutineScope(Dispatchers.Default).launch {

                    val markerJob = CoroutineScope(Dispatchers.Main).launch {
                        for (it in result) {
                            setMarkerOptions(it)
                        }
                    }
                    markerJob.join()
                    CoroutineScope(Dispatchers.Main).launch {
                        clusterManager.cluster()
                    }
                    loadingIndicator.dismissIndicator()
                }
                return result

            }

            // TODO 2. 마커 모두 삭제 버튼이 눌렸을 때 수행할 동작 정의
            if (isClickedMarkerRemoveButton) {
                isClickedMarkerRemoveButton = false

                clusterManager.removeItems(clusterManager.algorithm.items)
                CoroutineScope(Dispatchers.Main).launch {
                    clusterManager.cluster()
                    loadingIndicator.dismissIndicator()
                }
            }

            Log.e("UserGoogleMap : ", googleMap.cameraPosition.zoom.toString())
            Log.e("UserGoogleMap : ", googleMap.cameraPosition.tilt.toString())
            Log.e("UserGoogleMap : ", googleMap.cameraPosition.target.toString())
            Log.e("UserGoogleMap : ", googleMap.cameraPosition.bearing.toString())

            // TODO 3. 운동시작 버튼이 눌렸을 때 수행할 동작 정의
            if (isStartWorkoutButtonClicked) {

            }

            // TODO 4. 확대메뉴 항목 스피너 선택 시
            if (isSelectedZoomInMenuSpinner) {
                isSelectedZoomInMenuSpinner = false
                clusterManager.cluster()
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(googleMapsZoomLevel))
            }

            // TODO 사용자의 위치를 추적하여 마커를 출력하되, 이전 마커는 지워야 마커가 누적되지 않는다.
            googleMap.apply {
                currentMarker?.remove()
                currentMarker = addMarker(setUserMarkerOptions(latitude, longitude))
            }

            // TODO 초기에는 사용자의 위치는 계속 업데이트 해 줄것 -> 지도 정보를 계속 업데이트 하여 정확도를 높이는 방식인듯 함.
            if (isClickedLocationReturnButton || counter <= 3) {
                isClickedLocationReturnButton = false
                clusterManager.cluster()
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(Settings.GOOGLE_MAPS_ZOOM_LEVEL_DEFAULT))     // min:2f max:21f

                if (counter == 2) {
                    loadingIndicator.dismissIndicator()
                }
            }
        }
        return emptyList()
    }

    private fun addCircleToMarker(
        requestChar: String,
        latitude: Double,
        longitude: Double,
        scale: Double
    ) {

        // TODO 추후 커스터마이징
        when (requestChar) {
            "user" -> {
                val options = CircleOptions().apply {
                    center(LatLng(latitude, longitude))
                    radius(scale)
                    strokeColor(Color.RED)
                }
                markerCircleUserLocation = googleMap.addCircle(options)
            }
            "click" -> {
                val options = CircleOptions().apply {
                    center(LatLng(latitude, longitude))
                    radius(scale)
                    strokeColor(Color.RED)
                }
                //circleRadiusClickedItem = googleMap.addCircle(options)
            }
        }
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

    // TODO 마커클러스터 커스터마이징 필요. -> 현재는 HashMap 을 통하여 개별 마커에 대한 정보를 저장.
    // param 1 : List<ParkDB> 의 객체값중 하나를 받아와 item 을 생성해서 clusterManager 에 등록하여 마커 저장
    private fun setMarkerOptions(it: ParkDB) {
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
        val item = MyItem(it.latitude!!, it.longitude!!, parkName, parkCategory)
        parkNameSizeMap[parkName] = parkSize
        parkNameLatLngMap[parkName] = "${it.latitude} ${it.longitude}"
        clusterManager.addItem(item)
        // return markerOptions
    }
}