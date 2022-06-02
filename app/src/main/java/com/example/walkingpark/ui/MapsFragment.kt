package com.example.walkingpark.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.walkingpark.databinding.FragmentMapsBinding
import com.example.walkingpark.ui.view.LoadingIndicator
import com.example.walkingpark.ui.viewmodels.MainViewModel
import com.example.walkingpark.ui.viewmodels.MapsViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
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

        loadingIndicator = LoadingIndicator(requireContext(), "지도 초기화중...")
        loadingIndicator.startLoadingIndicator()

        mainViewModel.userLocation.observe(viewLifecycleOwner) {
            it?.let {
                mapsViewModel.requestUserLocationUpdate(LatLng(it.latitude, it.longitude))
            }
        }

        // LoadingIndicator 관련
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