package com.example.walkingpark.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.walkingpark.databinding.FragmentHomeBinding
import com.example.walkingpark.presentation.service.LocationService
import com.example.walkingpark.presentation.view.LoadingIndicator
import com.example.walkingpark.presentation.viewmodels.HomeViewModel
import com.example.walkingpark.presentation.viewmodels.MainViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment : Fragment() {

/*
    기존 뷰모델 생성법 : private val searchViewModel: SearchViewModel by viewModels()
    프래그먼트- 액티비티간 뷰모델 공유 : private val searchViewModel: SearchViewModel by activityViewModels()
    프래그먼트끼리 뷰모델 공유 : private val viewModel: ManageLocationViewModel by viewModels({requireParentFragment()})
*/

    private val homeViewModel: HomeViewModel by viewModels()
    private var binding: FragmentHomeBinding? = null
    private lateinit var loadingIndicator: LoadingIndicator
    private var isUiInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding?.homeViewModel = homeViewModel
        binding?.lifecycleOwner = this
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingIndicator = LoadingIndicator(requireContext(), "RestApi 통신중...")
        loadingIndicator.startLoadingIndicator()
        LocationService.userLocation.observe(viewLifecycleOwner) {
            if (!isUiInitialized) {
                isUiInitialized = true
                CoroutineScope(Dispatchers.Default).launch {
                    it?.let { it1 ->
                        homeViewModel.startStationApi(it1)
                    }
                }
                CoroutineScope(Dispatchers.Default).launch {
                    it?.let { it1 ->
                        homeViewModel.startWeatherApi(it1)
                    }
                }
            }
        }

        homeViewModel.userLiveHolderLoadedStatus.observe(viewLifecycleOwner){
            if (it["station"] == "success" && it["air"] == "success" && it["weather"] == "success")
                loadingIndicator.dismissIndicator()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}