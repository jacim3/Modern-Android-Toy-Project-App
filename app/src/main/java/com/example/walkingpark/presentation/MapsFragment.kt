package com.example.walkingpark.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.walkingpark.R
import com.example.walkingpark.databinding.FragmentMapsBinding
import com.example.walkingpark.presentation.service.LocationService
import com.example.walkingpark.presentation.view.LoadingIndicator
import com.example.walkingpark.presentation.viewmodels.MainViewModel
import com.example.walkingpark.presentation.viewmodels.MapsViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint

/*
*  뷰바인딩 사용 안함
*/
// TODO 클린아키텍쳐 엔티티 공부할것 -> 추후 GSON 을 통하여 받은 데이터를 한번 더 정리해야 함
//


@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val mapsViewModel: MapsViewModel by viewModels()
    private var binding: FragmentMapsBinding? = null
    private lateinit var loadingIndicator: LoadingIndicator

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(layoutInflater, container, false)
        binding!!.lifecycleOwner = this
        binding!!.mapsViewModel = mapsViewModel
        binding!!.mapFragment.onCreate(savedInstanceState)
        binding!!.mapFragment.getMapAsync(this)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*LoadingIndicator(requireActivity(), "지도정보 초기화 하는중...")
    googleMapsRepository.loadingIndicator.startLoadingIndicator()*/


/*        mainViewModel.userLiveHolderLatLng.observe(viewLifecycleOwner) {
            //parkMapsViewModel.getParkData(it["위도"]!!, it["경도"]!!)
        }*/

        loadingIndicator = LoadingIndicator(requireContext(), "지도 초기화중...")

        mapsViewModel.liveHolderSeekBar.observe(viewLifecycleOwner) {
            Log.e("eventTriggered !!!", it.toString())
        }

        LocationService.userLocation.observe(viewLifecycleOwner) {
            if (it != null) mapsViewModel.requestUserLocationUpdate(it)
        }

        mapsViewModel.liveHolderIndicatorFlag.observe(viewLifecycleOwner){
           when(it[0]) {
                "show" -> {
                    loadingIndicator.setDescription(it[1])
                    loadingIndicator.startLoadingIndicator()
                }
                "dismiss" -> {
                    loadingIndicator.dismissIndicator()
                }
           }
        }

/*        mapsViewModel.myGoogleMap.observe(viewLifecycleOwner) {
            mapsViewModel.onMapReady()
        }*/
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.e("ParkMapsFragment", "onViewStateRestored()")
    }

    override fun onStart() {
        super.onStart()
        binding!!.mapFragment.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding!!.mapFragment.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding!!.mapFragment.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding!!.mapFragment.onStop()
    }

    // 구글맵 준비가 완료되었음을 뷰모델에 전달. googleMap 은 viewModel 에서 처리할것 !
    override fun onMapReady(googleMap: GoogleMap) {
        mapsViewModel.onMapReady(googleMap)
    }
}