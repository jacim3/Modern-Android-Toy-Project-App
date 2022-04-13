package com.example.walkingpark.components.ui.fragment.tab_2

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
import com.example.walkingpark.MainViewModel
import com.example.walkingpark.components.ui.dialog.LoadingIndicator
import com.example.walkingpark.data.enum.Settings
import com.example.walkingpark.data.repository.GoogleMapsRepository
import com.example.walkingpark.databinding.FragmentParkmapsBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*
*  뷰바인딩 사용 안함
*/

@AndroidEntryPoint
class ParkMapsFragment : Fragment(), OnMapReadyCallback {

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
        mainViewModel.userLiveHolderLatLng.observe(viewLifecycleOwner) {
            parkMapsViewModel.getParkData(it["위도"]!!, it["경도"]!!)
        }
        googleMapsRepository.loadingIndicator =
            LoadingIndicator(requireActivity(), "지도정보 초기화 하는중...")
        googleMapsRepository.loadingIndicator.startLoadingIndicator()
        setSpinner()
        setButtons()
        setSeekBar()
    }

    private fun setSeekBar() {
        binding!!.seekBarSearchScale.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(bar: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(bar: SeekBar?) {
                googleMapsRepository.loadingIndicator.startLoadingIndicator()
                googleMapsRepository.getSearchRangeFromSeekBar = bar!!.progress
            }
        })
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
        }

        binding!!.buttonRemoveMarkers.setOnClickListener {
            googleMapsRepository.isClickedMarkerRemoveButton = true
            googleMapsRepository.loadingIndicator.startLoadingIndicator()
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
                                Settings.GOOGLE_MAPS__ZOOM_LEVEL_VERY_LOW
                            googleMapsRepository.isSelectedZoomInMenuSpinner = true
                        }
                        1 -> {
                            googleMapsRepository.googleMapsZoomLevel =
                                Settings.GOOGLE_MAPS__ZOOM_LEVEL_LOW
                            googleMapsRepository.isSelectedZoomInMenuSpinner = true
                        }
                        2 -> {
                            googleMapsRepository.googleMapsZoomLevel =
                                Settings.GOOGLE_MAPS__ZOOM_LEVEL_DEFAULT
                            googleMapsRepository.isSelectedZoomInMenuSpinner = true

                        }
                        3 -> {
                            googleMapsRepository.googleMapsZoomLevel =
                                Settings.GOOGLE_MAPS__ZOOM_LEVEL_HIGH
                            googleMapsRepository.isSelectedZoomInMenuSpinner = true

                        }
                        4 -> {
                            googleMapsRepository.googleMapsZoomLevel =
                                Settings.GOOGLE_MAPS__ZOOM_LEVEL_VERY_HIGH
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

    companion object {
        fun newInstance() = ParkMapsFragment()
    }

    // 구글맵 준비가 완료된 이후 수행 및 GoogleMap 관련 비즈니스 로직을 GoogleMapRepository 로 옮기기
    override fun onMapReady(googleMap: GoogleMap) {
        googleMapsRepository.isMapLoadCompleted = true
        googleMapsRepository.googleMap = googleMap
        googleMapsRepository.clusterManager =
            ClusterManager(context, googleMapsRepository.googleMap)
        googleMapsRepository.googleMap.setOnCameraIdleListener(googleMapsRepository.clusterManager)
    }
}