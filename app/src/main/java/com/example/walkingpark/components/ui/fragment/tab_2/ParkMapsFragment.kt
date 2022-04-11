package com.example.walkingpark.components.ui.fragment.tab_2

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.walkingpark.MainViewModel
import com.example.walkingpark.components.ui.dialog.LoadingIndicator
import com.example.walkingpark.databinding.FragmentParkmapsBinding
import com.example.walkingpark.data.repository.GoogleMapsRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
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
    private lateinit var indicator:LoadingIndicator

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
        Log.e("ParkMapsFragment()", "onViewCreated()")

        //Log.e("ParkMapsService()", (activity as MainActivity).parkMapsService.number.toString())

        indicator = LoadingIndicator(requireActivity())
        // mainViewModel 에 lazy 초기화한 콜백함수에 의하여 계속 observe 로 사용자 위치를 출력한다.
        mainViewModel.userLiveHolderLatLng.observe(viewLifecycleOwner){
            parkMapsViewModel.getParkData(it["위도"]!!, it["경도"]!!, indicator)
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
    }

    private fun getCurrentPosition(){

    }
}