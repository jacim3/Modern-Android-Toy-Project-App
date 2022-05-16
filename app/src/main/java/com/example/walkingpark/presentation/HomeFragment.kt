package com.example.walkingpark.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.walkingpark.constants.Common
import com.example.walkingpark.databinding.FragmentHomeBinding
import com.example.walkingpark.presentation.view.LoadingIndicator
import com.example.walkingpark.presentation.viewmodels.HomeViewModel
import com.example.walkingpark.presentation.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

/*
    기존 뷰모델 생성법 : private val searchViewModel: SearchViewModel by viewModels()
    프래그먼트- 액티비티간 뷰모델 공유 : private val searchViewModel: SearchViewModel by activityViewModels()
    프래그먼트끼리 뷰모델 공유 : private val viewModel: ManageLocationViewModel by viewModels({requireParentFragment()})
*/

    private val homeViewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private var binding: FragmentHomeBinding? = null
    private lateinit var loadingIndicator: LoadingIndicator
    private lateinit var a: Disposable
    private lateinit var b: io.reactivex.rxjava3.disposables.Disposable
    private lateinit var c: Disposable
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

        // 사용자 위치업데이트 관찰 수행시 수행.
        mainViewModel.userLocation.observe(viewLifecycleOwner) {

            homeViewModel.isWeatherLoaded.apply {
                if (this.value != Common.RESPONSE_RECEIVE_PROCESSING &&
                    this.value != Common.RESPONSE_RECEIVE_SUCCESS
                ) {
                    this.postValue(Common.RESPONSE_RECEIVE_PROCESSING)
                    a = homeViewModel.startWeatherApi(it)
                }
            }

            homeViewModel.isStationLoaded.apply {
                if (this.value != Common.RESPONSE_RECEIVE_PROCESSING &&
                    this.value != Common.RESPONSE_RECEIVE_SUCCESS
                ) {
                    this.postValue(Common.RESPONSE_RECEIVE_PROCESSING)
                    b = homeViewModel.startGeocodingBeforeStationApi(it)
                }
            }
        }

        homeViewModel.userResponseCheck.observe(viewLifecycleOwner) {

            if (it.station == Common.RESPONSE_RECEIVE_SUCCESS &&
                    it.air != Common.RESPONSE_RECEIVE_PROCESSING &&
                    it.air != Common.RESPONSE_RECEIVE_SUCCESS) {
                homeViewModel.userLiveHolderStation.value?.stationName?.let { name ->
                    homeViewModel.isAirLoaded.postValue(Common.RESPONSE_RECEIVE_PROCESSING)
                    homeViewModel.startAirApi(name)
                }
            }

            if (it.air == Common.RESPONSE_RECEIVE_SUCCESS &&
                it.weather == Common.RESPONSE_RECEIVE_SUCCESS
            ) {
                loadingIndicator.dismissIndicator()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}