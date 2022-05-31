package com.example.walkingpark.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walkingpark.constants.Common
import com.example.walkingpark.databinding.FragmentHomeBinding
import com.example.walkingpark.presentation.adapter.home.HumidityAdapter
import com.example.walkingpark.presentation.adapter.home.WeatherAdapter
import com.example.walkingpark.presentation.adapter.home.WindAdapter
import com.example.walkingpark.presentation.view.LoadingIndicator
import com.example.walkingpark.presentation.viewmodels.HomeViewModel
import com.example.walkingpark.presentation.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


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
    private lateinit var humidityAdapterAdapter: HumidityAdapter
    private lateinit var weatherAdapterAdapter: WeatherAdapter
    private lateinit var windAdapterAdapter: WindAdapter

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
                if (this.value != Common.RESPONSE_PROCEEDING &&
                    this.value != Common.RESPONSE_SUCCESS
                ) {
                    this.postValue(Common.RESPONSE_PROCEEDING)
                    homeViewModel.startWeatherApi(it)
                }
            }

            homeViewModel.isStationLoaded.apply {
                if (this.value != Common.RESPONSE_PROCEEDING &&
                    this.value != Common.RESPONSE_SUCCESS
                ) {
                    this.postValue(Common.RESPONSE_PROCEEDING)
                    homeViewModel.startGeocodingBeforeStationApi(it)
                }
            }
        }

        homeViewModel.userResponseCheck.observe(viewLifecycleOwner) {

            if (it.station == Common.RESPONSE_SUCCESS &&
                it.air != Common.RESPONSE_PROCEEDING &&
                it.air != Common.RESPONSE_SUCCESS
            ) {
                homeViewModel.userLiveHolderStation.value?.stationName?.let { name ->
                    homeViewModel.isAirLoaded.postValue(Common.RESPONSE_PROCEEDING)
                    homeViewModel.startAirApi(name)
                }
            }

            if (it.air == Common.RESPONSE_SUCCESS &&
                it.weather == Common.RESPONSE_SUCCESS
            ) {
                loadingIndicator.dismissIndicator()

            }
        }

        binding?.let {

            it.buttonTabWeather.setOnClickListener {
                setContainerVisibility(0)
            }

            it.buttonTabWind.setOnClickListener {
                setContainerVisibility(1)
            }

            it.buttonTabHumidity.setOnClickListener {
                setContainerVisibility(2)
            }
        }

        setAdapters()
    }

    private fun setContainerVisibility(code: Int){
        when (code) {
            0 -> {
                binding?.weatherPanelContainer?.visibility = View.VISIBLE
                binding?.windPanelContainer?.visibility = View.GONE
                binding?.humidityPanelContainer?.visibility = View.GONE
            }
            1 -> {
                binding?.weatherPanelContainer?.visibility = View.GONE
                binding?.windPanelContainer?.visibility = View.VISIBLE
                binding?.humidityPanelContainer?.visibility = View.GONE
            }
            2 -> {
                binding?.weatherPanelContainer?.visibility = View.GONE
                binding?.windPanelContainer?.visibility = View.GONE
                binding?.humidityPanelContainer?.visibility = View.VISIBLE
            }
        }
    }

    private fun setAdapters(){
        weatherAdapterAdapter = WeatherAdapter()
        humidityAdapterAdapter = HumidityAdapter()
        windAdapterAdapter = WindAdapter()

        binding?.let {
            it.recyclerViewWeather.apply {
                this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = weatherAdapterAdapter
            }

            it.recyclerViewHumidity.apply {
                this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = humidityAdapterAdapter
            }

            it.recyclerViewWind.apply {
                this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = windAdapterAdapter
            }
        }

        homeViewModel.userLiveHolderWeather.observe(viewLifecycleOwner) {
            weatherAdapterAdapter.setAdapterData(it)
            humidityAdapterAdapter.setAdapterData(it)
            windAdapterAdapter.setAdapterData(it)
        }

        homeViewModel.userLiveHolderAir.observe(viewLifecycleOwner) {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object{
        @JvmStatic
        @BindingAdapter("bindingSrc")
        fun loadImage(imageView: AppCompatImageView, resId: Int) {
            imageView.setImageResource(resId)
        }
    }
}