package com.example.walkingpark.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.walkingpark.presentation.viewmodels.ParkMapsViewModel
import com.example.walkingpark.constants.Settings
import com.example.walkingpark.data.repository.GoogleMapsRepository
import com.example.walkingpark.databinding.FragmentParkmapsBinding
import com.example.walkingpark.presentation.viewmodels.MainViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*
*  뷰바인딩 사용 안함
*/
// TODO 클린아키텍쳐 엔티티 공부할것 -> 추후 GSON 을 통하여 받은 데이터를 한번 더 정리해야 함
//


@AndroidEntryPoint
class ParkMapsFragment : Fragment(), OnMapReadyCallback {


    // TODO ParkMapsFragment 에서 Repository 에 직접 의존하는 코드를 제거하기 위한 리팩토링 필요.
    @Inject
    lateinit var googleMapsRepository: GoogleMapsRepository

    private val mainViewModel: MainViewModel by activityViewModels()
    private val parkMapsViewModel: ParkMapsViewModel by viewModels()
    private var binding: FragmentParkmapsBinding? = null

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

            /*LoadingIndicator(requireActivity(), "지도정보 초기화 하는중...")
        googleMapsRepository.loadingIndicator.startLoadingIndicator()*/
        setSpinner()
        setButtons()
        setSeekBar()

        mainViewModel.userLiveHolderLatLng.observe(viewLifecycleOwner) {
            //parkMapsViewModel.getParkData(it["위도"]!!, it["경도"]!!)
        }

        parkMapsViewModel.liveHolderSeekBar.observe(viewLifecycleOwner){
            Log.e("eventTriggered !!!", it.toString())
        }
    }

    private fun setSeekBar() {
        binding!!.seekBarSearchScale.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(bar: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(bar: SeekBar?) {
                parkMapsViewModel.liveHolderSeekBar.value = bar?.progress
                googleMapsRepository.loadingIndicator.startLoadingIndicator()
                googleMapsRepository.loadingIndicator.setDescription("검색범위 재 설정 하는중")
                googleMapsRepository.loadingIndicator.flag = "rangeChange"
                googleMapsRepository.getSearchRangeFromSeekBar = bar!!.progress
            }
        })
        binding!!.seekBarSearchScale.progress = 3
    }

    private fun setButtons() {
        binding!!.buttonChaseCamera.setOnClickListener {
            googleMapsRepository.isClickedLocationReturnButton = true
        }

        binding!!.buttonStartWorkout.setOnClickListener {
        }

        binding!!.buttonPrintMarkers.setOnClickListener {
            googleMapsRepository.isClickedMarkerPrintButton = true
            googleMapsRepository.loadingIndicator.startLoadingIndicator()
            googleMapsRepository.loadingIndicator.setDescription("마커 찍는중")

        }

        binding!!.buttonRemoveMarkers.setOnClickListener {
            googleMapsRepository.isClickedMarkerRemoveButton = true
            googleMapsRepository.loadingIndicator.startLoadingIndicator()
            googleMapsRepository.loadingIndicator.setDescription("마커 삭제하는중")
        }
    }

    // 맵 축소 관련 스피너
    private fun setSpinner() {
        val options = arrayOf("작게 축소", "축소", "보통", "확대", "크게 확대")
        val spinnerOptions =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, options)
        binding!!.spinnerMapsZooming.adapter = spinnerOptions
        binding!!.spinnerMapsZooming.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {

                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    when (position) {
                        0 -> {
                            googleMapsRepository.googleMapsZoomLevel =
                                Settings.GOOGLE_MAPS_ZOOM_LEVEL_VERY_HIGH
                            googleMapsRepository.isSelectedZoomInMenuSpinner = true
                        }
                        1 -> {
                            googleMapsRepository.googleMapsZoomLevel =
                                Settings.GOOGLE_MAPS_ZOOM_LEVEL_HIGH
                            googleMapsRepository.isSelectedZoomInMenuSpinner = true
                        }
                        2 -> {
                            googleMapsRepository.googleMapsZoomLevel =
                                Settings.GOOGLE_MAPS_ZOOM_LEVEL_DEFAULT
                            googleMapsRepository.isSelectedZoomInMenuSpinner = true
                        }
                        3 -> {
                            googleMapsRepository.googleMapsZoomLevel =
                                Settings.GOOGLE_MAPS_ZOOM_LEVEL_LOW
                            googleMapsRepository.isSelectedZoomInMenuSpinner = true
                        }
                        4 -> {
                            googleMapsRepository.googleMapsZoomLevel =
                                Settings.GOOGLE_MAPS_ZOOM_LEVEL_VERY_LOW
                            googleMapsRepository.isSelectedZoomInMenuSpinner = true
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

                override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    TODO("Not yet implemented")
                }
            }
        binding!!.spinnerMapsZooming.setSelection(2)
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

    // 구글맵 준비가 완료된 이후 수행 및 GoogleMap 관련 구체적인 비즈니스 로직은 GoogleMapsRepository 에서 수행
    override fun onMapReady(googleMap: GoogleMap) {
        googleMapsRepository.isMapLoadCompleted = true
        googleMapsRepository.googleMap = googleMap
        googleMapsRepository.clusterManager =
            ClusterManager(context, googleMapsRepository.googleMap)
        googleMapsRepository.onMapReady()
    }
}