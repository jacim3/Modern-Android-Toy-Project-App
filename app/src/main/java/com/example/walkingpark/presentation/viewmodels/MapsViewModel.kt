package com.example.walkingpark.presentation.viewmodels

import android.app.Application
import android.graphics.Camera
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.walkingpark.R
import com.example.walkingpark.constants.Common
import com.example.walkingpark.constants.Settings
import com.example.walkingpark.data.source.room.ParkDB
import com.example.walkingpark.domain.model.MarkerItem
import com.example.walkingpark.domain.usecase.maps.db.ParsingItemUseCase
import com.example.walkingpark.domain.usecase.maps.db.parent.ResultDataBaseUseCase
import com.example.walkingpark.presentation.view.LoadingIndicator
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.sqrt

@HiltViewModel
class MapsViewModel @Inject constructor(
    application: Application,
    private val parsingItemUseCase: ParsingItemUseCase,
    private val resultDataBaseUseCase: ResultDataBaseUseCase
) : AndroidViewModel(application) {

    val liveHolderParkData = MutableLiveData<List<ParkDB>>()
    val liveHolderSeekBar = MutableLiveData<Int>()
    val liveHolderMapsZoomLevel = MutableLiveData<Double>()
    val liveHolderIndicatorFlag = MutableLiveData<Array<String>>()

    var userMarker: Marker? = null
    var userMarkerCircle: Circle? = null

    lateinit var userLatLng: LatLng

    var parkMarkers: Marker? = null
    var parkMarkerCircle: Circle? = null
    lateinit var myGoogleMap: GoogleMap

    private var isButtonZoomInClicked = false
    private var isButtonZoomOutClicked = false

    private var isButtonRemoveMarkerClicked = false
    private var isButtonAddMarkerClicked = false

    private var isButtonReturnClicked = false
    private var isButtonWorkoutClicked = false

    private var mapsUpdateCount = 0


    val myLocationLatLng = ObservableField<LatLng>()
    val otherLocationsLatLng = ObservableField<MutableList<LatLng>>()

    private var isOnMapReadyCalled = false

    lateinit var clusterManager: ClusterManager<MarkerItem>
    private var markerCircleClusterItem: Circle? = null


    // TODO 초기에는 사용자의 위치는 계속 업데이트 해 줄것 -> 지도 정보를 계속 업데이트 하여 정확도를 높이는 방식인듯 함.
    fun requestUserLocationUpdate(latLng: LatLng) {

        if (!isOnMapReadyCalled) return

        mapsUpdateCount++

        userMarkerCircle?.remove()
        drawMarkerCircle("사용자", latLng, liveHolderSeekBar.value?.times(1000.0) ?: 1000.0)

        if (isButtonAddMarkerClicked) {

            isButtonAddMarkerClicked = false
            liveHolderIndicatorFlag.value = arrayOf("show", "마커 추가...")

            myGoogleMap.setOnCameraIdleListener(clusterManager)

            var response = emptyList<ParkDB>()
            var mult = 0
            var responseMap = HashMap<String, Any>()
            viewModelScope.launch {
                while (response.isEmpty()) {
                    mult ++
                    responseMap =
                        liveHolderSeekBar.value?.let { resultDataBaseUseCase(latLng, it, mult) }!!
                }
                response = responseMap["response"] as List<ParkDB>
                liveHolderSeekBar.value = responseMap["mult"] as Int

                response.forEach {
                    clusterManager.addItem(parsingItemUseCase(it))
                }
                clusterManager.cluster()
                liveHolderIndicatorFlag.value = arrayOf("dismiss", "")
            }
        }

        if (isButtonRemoveMarkerClicked) {

            isButtonRemoveMarkerClicked = false
            liveHolderIndicatorFlag.value = arrayOf("show", "마커삭제...")

            clusterManager.removeItems(clusterManager.algorithm.items)
            viewModelScope.launch {
                clusterManager.cluster()
                liveHolderIndicatorFlag.value = arrayOf("dismiss", "")
            }
        }

        if (isButtonZoomInClicked) {
            isButtonZoomInClicked = false
            myGoogleMap.moveCamera(CameraUpdateFactory.zoomIn())
        }

        if (isButtonZoomOutClicked) {
            isButtonZoomOutClicked = false
            myGoogleMap.moveCamera(CameraUpdateFactory.zoomOut())
        }

        myGoogleMap.apply {
            userMarker?.remove()
            userMarker = addMarker(setUserLocationMarker(latLng))
        }

        myGoogleMap.moveCamera(
            CameraUpdateFactory.newLatLng(
                LatLng(
                    latLng.latitude,
                    latLng.longitude
                )
            )
        )

        if (isButtonReturnClicked|| mapsUpdateCount <= 3) {
            isButtonReturnClicked = false
            clusterManager.cluster()
            myGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            myGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(Settings.GOOGLE_MAPS_ZOOM_LEVEL_DEFAULT))     // min:2f max:21f

            if (mapsUpdateCount == 2) {
                liveHolderIndicatorFlag.value = arrayOf("dismiss", "")
            }
        }
    }

    fun onMapReady(googleMap: GoogleMap) {
        isOnMapReadyCalled = true
        myGoogleMap = googleMap
        setMarkerClustering()
    }

    // ---------------------------------------------------------------------------------------------
    // -------------------------------------- private Methods --------------------------------------
    // ---------------------------------------------------------------------------------------------

    private fun setUserLocationMarker(latLng: LatLng): MarkerOptions {
        val markerOptions = MarkerOptions()
        markerOptions.position(LatLng(latLng.latitude, latLng.longitude))
        markerOptions.title("내 위치")
        markerOptions.snippet("TODO 주소")
        markerOptions.draggable(true)
        // markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        return markerOptions
    }

    private fun setMarkerClustering() {

        clusterManager = ClusterManager(getApplication(), myGoogleMap)
        myGoogleMap.setOnCameraIdleListener(clusterManager)

        clusterManager.setOnClusterItemClickListener {
            markerCircleClusterItem?.remove()

            // 각각의 마커정보에 대하여 DB 에서 제공하는 면적 정보를 가져와, 반지름으로 변환.
            ////TODO USECASE
            markerCircleClusterItem = myGoogleMap.addCircle(CircleOptions().apply {
                val latLng =
                    center(LatLng(it.position.latitude, it.position.longitude))
                radius(sqrt(it.size / PI))
                strokeColor(Color.YELLOW)
            })
            false
        }
        clusterManager.setOnClusterClickListener {
            myGoogleMap.moveCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(
                        it.position.latitude,
                        it.position.longitude
                    )
                )
            )
/*            googleMap.animateCamera(CameraUpdateFactory.zoomTo(Settings.GOOGLE_MAPS__ZOOM_LEVEL_HIGH))
            clusterManager.clusterMarkerCollection.markers.*/
            false
        }
    }

    private fun drawMarkerCircle(
        request: String,
        latLng: LatLng,
        scale: Double
    ) {

        // TODO 추후 커스터마이징
        when (request) {
            "사용자" -> {
                val options = CircleOptions().apply {
                    center(latLng)
                    radius(scale)
                    strokeColor(Color.RED)
                }
                // markerCircleUserLocation = googleMap.addCircle(options)
            }
            "공원" -> {
                val options = CircleOptions().apply {
                    center(latLng)
                    radius(scale)
                    strokeColor(Color.RED)
                }
                //circleRadiusClickedItem = googleMap.addCircle(options)
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // -------------------------------------- 데이터 바인딩 관련 --------------------------------------
    // ---------------------------------------------------------------------------------------------

    fun setButtonEventHandler(view: View) {
        when (view.id) {
            R.id.buttonPrintMarkers -> {
                isButtonAddMarkerClicked = true
            }

            R.id.buttonRemoveMarkers -> {
                clusterManager.removeItems(clusterManager.algorithm.items)
                clusterManager.cluster()
                isButtonRemoveMarkerClicked = true
            }

            R.id.buttonToReturn -> {
                isButtonReturnClicked = true
            }

            R.id.buttonZoomIn -> {

                isButtonZoomInClicked = true
                clusterManager.cluster()
                myGoogleMap.moveCamera(
                    CameraUpdateFactory.newLatLng(
                        LatLng(
                            userLatLng.latitude,
                            userLatLng.longitude
                        )
                    )
                )
            }

            R.id.buttonZoomOut -> {
                isButtonZoomOutClicked = true
            }

            R.id.buttonStartWorkout -> {
                isButtonWorkoutClicked = true
            }
        }
    }

    fun onSeekBarChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        liveHolderSeekBar.value = progress
    }
}