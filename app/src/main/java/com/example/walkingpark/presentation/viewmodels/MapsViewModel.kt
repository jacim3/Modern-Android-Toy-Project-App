package com.example.walkingpark.presentation.viewmodels

import android.app.Application
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.SeekBar
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.walkingpark.R
import com.example.walkingpark.constants.Settings
import com.example.walkingpark.data.source.room.ParkDB
import com.example.walkingpark.domain.model.MarkerItem
import com.example.walkingpark.domain.usecase.maps.db.normal.ParsingItemUseCase
import com.example.walkingpark.domain.usecase.maps.db.parent.ResultDataBaseUseCase
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.sqrt

// SocketTimeoutException
@HiltViewModel
class MapsViewModel @Inject constructor(
    application: Application,
    private val parsingItemUseCase: ParsingItemUseCase,
    private val resultDataBaseUseCase: ResultDataBaseUseCase
) : AndroidViewModel(application) {

    val liveHolderParkData = MutableLiveData<List<ParkDB>>()
    val liveHolderSeekBar = MutableLiveData<Int>().apply {
        this.postValue(3)
    }
    val liveHolderMapsZoomLevel = MutableLiveData<Double>()
    val liveHolderIndicatorFlag = MutableLiveData<Array<String>>()

    private var userMarker: Marker? = null
    private var userMarkerCircle: Circle? = null

    lateinit var userLatLng: LatLng

    var parkMarkers: Marker? = null
    var parkMarkerCircle: Circle? = null
    lateinit var myGoogleMap: GoogleMap

    private var mapsUpdateCount = 0
    private var isOnMapReadyCalled = false

    lateinit var clusterManager: ClusterManager<MarkerItem>


    fun requestUserLocationUpdate(latLng: LatLng) {

        if (!isOnMapReadyCalled) return

        mapsUpdateCount++
        userLatLng = latLng

        drawMarkerCircle("사용자", latLng, liveHolderSeekBar.value!!.times(1000.0))

        myGoogleMap.apply {
            userMarker?.remove()
            userMarker = addMarker(setUserLocationMarker(latLng))
        }

        if (mapsUpdateCount <= 2) {
            clusterManager.cluster()
            myGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            myGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(Settings.GOOGLE_MAPS_ZOOM_LEVEL_DEFAULT))     // min:2f max:21f

            if (mapsUpdateCount == 2) {
                setLoadingIndicator("dismiss", "")
            }
        }
    }

    fun onMapReady(googleMap: GoogleMap) {
        isOnMapReadyCalled = true
        myGoogleMap = googleMap
        setMarkerClustering()
    }

// -------------------------------------------------------------------------------------------------
// ---------------------------------------- private Methods ----------------------------------------
// -------------------------------------------------------------------------------------------------

    private fun setLoadingIndicator(command: String, text: String) {
        liveHolderIndicatorFlag.value = arrayOf(command, text)
    }

    private fun setUserLocationMarker(latLng: LatLng): MarkerOptions {
        val markerOptions = MarkerOptions()
        markerOptions.position(LatLng(latLng.latitude, latLng.longitude))
        markerOptions.title("내 위치")
        markerOptions.snippet("TODO 주소")
        markerOptions.draggable(true)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        return markerOptions
    }

    private fun setMarkerClustering() {

        clusterManager = ClusterManager(getApplication(), myGoogleMap)
        myGoogleMap.setOnCameraIdleListener(clusterManager)

        clusterManager.setOnClusterItemClickListener {
            parkMarkerCircle?.remove()

            // 각각의 마커정보에 대하여 DB 에서 제공하는 면적 정보를 가져와, 반지름으로 변환.
            parkMarkerCircle = myGoogleMap.addCircle(CircleOptions().apply {
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
            false
        }

        myGoogleMap.setOnMapClickListener {
            parkMarkerCircle?.remove()
        }

        myGoogleMap.setOnMapLongClickListener {

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
                userMarkerCircle?.remove()

                val options = CircleOptions().apply {
                    center(latLng)
                    radius(scale)
                    strokeColor(Color.RED)
                }
                userMarkerCircle = myGoogleMap.addCircle(options)
            }
            "공원" -> {
                val options = CircleOptions().apply {
                    center(latLng)
                    radius(scale)
                    strokeColor(Color.RED)
                }
                userMarkerCircle = myGoogleMap.addCircle(options)
            }
        }
    }

    private suspend fun addMarkers() {

        setLoadingIndicator("show", "마커추가...")

        if (clusterManager.algorithm.items.isNotEmpty()) {
            clusterManager.removeItems(clusterManager.algorithm.items)
        }

        myGoogleMap.setOnCameraIdleListener(clusterManager)

        var response = emptyList<ParkDB>()
        var mult = -1
        var responseMap = HashMap<String, Any>()

        while (responseMap.isNullOrEmpty()) {
            mult++
            responseMap =
                liveHolderSeekBar.value?.let {
                    resultDataBaseUseCase(
                        userLatLng,
                        it,
                        mult
                    )
                }!!
        }

        Log.e("asdfasdfasdf", responseMap["mult"].toString())

        response = responseMap["response"] as List<ParkDB>
        liveHolderSeekBar.value = responseMap["mult"] as Int

        response.forEach {
            clusterManager.addItem(parsingItemUseCase(it))
        }
        clusterManager.cluster()
        setLoadingIndicator("dismiss", "")
    }


    private fun removeMarkers() {
        setLoadingIndicator("show", "마커삭제...")

        if (clusterManager.algorithm.items.isNotEmpty()) {
            clusterManager.removeItems(clusterManager.algorithm.items)
        }
        clusterManager.cluster()
        setLoadingIndicator("dismiss", "")
    }

// -------------------------------------------------------------------------------------------------
// ----------------------------------------- DataBinding -------------------------------------------
// -------------------------------------------------------------------------------------------------

    // 버튼 클릭 핸들러
    fun setButtonEventHandler(view: View) {
        when (view.id) {

            R.id.buttonPrintMarkers -> {
                viewModelScope.launch {
                    addMarkers()
                }
            }

            R.id.buttonRemoveMarkers -> {
                removeMarkers()
            }

            R.id.buttonToReturn -> {
                try {
                    myGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLng))
                } catch (e: UninitializedPropertyAccessException) {
                }
                myGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(Settings.GOOGLE_MAPS_ZOOM_LEVEL_DEFAULT))
            }

            R.id.buttonZoomIn -> {

                var currentZoom = myGoogleMap.cameraPosition.zoom + 1f

                if (currentZoom >= myGoogleMap.maxZoomLevel)
                    currentZoom = myGoogleMap.maxZoomLevel
                clusterManager.cluster()
                myGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom))
            }

            R.id.buttonZoomOut -> {
                var currentZoom = myGoogleMap.cameraPosition.zoom - 1f

                if (currentZoom <= myGoogleMap.minZoomLevel)
                    currentZoom = myGoogleMap.minZoomLevel
                clusterManager.cluster()
                myGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom))
            }

            R.id.buttonStartWorkout -> {

            }
        }
    }

    fun onSeekBarChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        liveHolderSeekBar.value = progress

        try {
            drawMarkerCircle("사용자", userLatLng, progress * 1000.0)
        } catch (e: UninitializedPropertyAccessException) {

        }

        if (clusterManager.algorithm.items.isNotEmpty()) {
            removeMarkers()
            viewModelScope.launch {
                addMarkers()
            }
        }
    }
}