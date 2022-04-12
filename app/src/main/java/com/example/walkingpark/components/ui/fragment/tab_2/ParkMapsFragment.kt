package com.example.walkingpark.components.ui.fragment.tab_2

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.walkingpark.MainViewModel
import com.example.walkingpark.components.ui.dialog.LoadingIndicator
import com.example.walkingpark.data.repository.GoogleMapsRepository
import com.example.walkingpark.data.tools.MyItem
import com.example.walkingpark.databinding.FragmentParkmapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*
*  뷰바인딩 사용 안함
*/

@AndroidEntryPoint
class ParkMapsFragment : Fragment(), OnMapReadyCallback{

    @Inject
    lateinit var googleMapsRepository: GoogleMapsRepository

    private val mainViewModel:MainViewModel by activityViewModels()
    private val parkMapsViewModel:ParkMapsViewModel by viewModels()
    private var binding:FragmentParkmapsBinding? = null

    private lateinit var googleMap:GoogleMap
    lateinit var clusterManager: ClusterManager<ClusterItem>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e("ParkMapsFragment()", "onAttach()")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParkmapsBinding.inflate(layoutInflater, container, false)
        binding!!.mapFragment.onCreate(savedInstanceState)
        binding!!.mapFragment.getMapAsync(this)
        Log.e("ParkMapsFragment()", "onCreateView()")
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel = ViewModelProvider(this)[ParkMapsViewModel::class.java]
        // mainViewModel 에 lazy 초기화한 콜백함수에 의하여 계속 observe 로 사용자 위치를 출력한다.
        mainViewModel.userLiveHolderLatLng.observe(viewLifecycleOwner){
            parkMapsViewModel.getParkData(it["위도"]!!, it["경도"]!!)
        }
        googleMapsRepository.loadingIndicator = LoadingIndicator(requireActivity())
        // 지도 ZoomIn 관련 스피너 설정
        val options = arrayOf(1,2,3,4,5)
        val spinnerOptions = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, options)
        binding!!.spinnerMapsZooming.adapter = spinnerOptions
        binding!!.spinnerMapsZooming.onItemSelectedListener = object :AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                when(position) {
                    0 -> {googleMapsRepository.googleMapsZoomLevel = 9.0f}
                    1 -> {googleMapsRepository.googleMapsZoomLevel = 11.0f}
                    2 -> {googleMapsRepository.googleMapsZoomLevel = 13.0f}
                    3 -> {googleMapsRepository.googleMapsZoomLevel = 15.0f}
                    4 -> {googleMapsRepository.googleMapsZoomLevel = 17.0f}
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                TODO("Not yet implemented")
            }
        }

        binding!!.buttonChaseCamera.setOnClickListener {
            googleMapsRepository.isGoogleMapChasingCamera = !googleMapsRepository.isGoogleMapChasingCamera
            if (googleMapsRepository.isGoogleMapChasingCamera) {
                binding!!.buttonChaseCamera.text = "카메라체이스 : ON"
            } else
                binding!!.buttonChaseCamera.text = "카메라체이스 : OFF"
        }

        binding!!.buttonStartWorkout.setOnClickListener {
        }

        binding!!.buttonPrintMarkers.setOnClickListener {
            googleMapsRepository.isAllMarkerPrintButtonClicked = true
            googleMapsRepository.loadingIndicator.startLoadingIndicator()
        }

        binding!!.buttonRemoveMarkers.setOnClickListener {
            googleMapsRepository.isAllMarkerRemoveButtonClicked = true
            googleMapsRepository.loadingIndicator.startLoadingIndicator()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.e("ParkMapsFragment", "onViewStateRestored()")
    }

    override fun onStart() {
        super.onStart()
        Log.e("ParkMapsFragment()", "onStart()")
        binding!!.mapFragment.onStart()
    }

    override fun onResume() {
        super.onResume()
        Log.e("ParkMapsFragment()", "onResume()")
        binding!!.mapFragment.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.e("ParkMapsFragment()", "onPause()")
        binding!!.mapFragment.onPause()
    }

    override fun onStop() {
        super.onStop()
        Log.e("ParkMapsFragment()", "onStop()")
        binding!!.mapFragment.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("ParkMapsFragment()", "onDestroyView()")
    }

    companion object {
        fun newInstance() = ParkMapsFragment()
    }

    // 구글맵 준비가 완료된 이후 수행되는 콜백메서드.
    override fun onMapReady(googleMap: GoogleMap) {
        //this.googleMap = googleMap
        Log.e("onMapReadyCallback", "executed!!!!")
        googleMapsRepository.isMapLoaded = true
        googleMapsRepository.googleMap = googleMap
        googleMapsRepository.clusterManager = ClusterManager(context, googleMapsRepository.googleMap)
        googleMapsRepository.googleMap.setOnCameraIdleListener(googleMapsRepository.clusterManager)
    // setUpClusterer(googleMap)
    }

    private fun setUpClusterer(map:GoogleMap) {
        // Position the map.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(51.503186, -0.126446), 10f))

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = ClusterManager(context, map)

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        // map.setOnMarkerClickListener(clusterManager)

        // Add cluster items (markers) to the cluster manager.
        addItems()
    }

    private fun addItems() {

        // Set some lat/lng coordinates to start with.
        var lat = 51.5145160
        var lng = -0.1270060

        // Add ten cluster items in close proximity, for purposes of this example.
        for (i in 0..9) {
            val offset = i / 60.0
            lat += offset
            lng += offset
            val offsetItem =
                MyItem(lat, lng, "Title $i", "Snippet $i")
            clusterManager.addItem(offsetItem)
        }
    }

}