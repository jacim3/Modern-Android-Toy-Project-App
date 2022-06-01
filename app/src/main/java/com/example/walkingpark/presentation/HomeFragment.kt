package com.example.walkingpark.presentation

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.walkingpark.constants.Common
import com.example.walkingpark.data.model.dto.WeatherDTO
import com.example.walkingpark.databinding.FragmentHomeBinding
import com.example.walkingpark.presentation.adapter.home.*
import com.example.walkingpark.presentation.view.LoadingIndicator
import com.example.walkingpark.presentation.viewmodels.HomeViewModel
import com.example.walkingpark.presentation.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

val label = arrayOf("오늘", "내일", "모레", "글피")     // 레이블 텍스트
const val diff = 24 * 60 * 60 * 1000       // calendar 간 날짜계산을 위해 필요

@AndroidEntryPoint
class HomeFragment : Fragment(), GetScrollItemListener {
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    private fun setContainerVisibility(code: Int) {
        when (code) {
            0 -> {
                binding?.apply {
                    weatherPanelContainer.visibility = View.VISIBLE
                    windPanelContainer.visibility = View.GONE
                    humidityPanelContainer.visibility = View.GONE
                }
            }
            1 -> {
                binding?.apply {
                    weatherPanelContainer.visibility = View.GONE
                    windPanelContainer.visibility = View.VISIBLE
                    humidityPanelContainer.visibility = View.GONE
                }
            }
            2 -> {
                binding?.apply {
                    weatherPanelContainer.visibility = View.GONE
                    windPanelContainer.visibility = View.GONE
                    humidityPanelContainer.visibility = View.VISIBLE
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setAdapters() {
        weatherAdapterAdapter = WeatherAdapter()
        humidityAdapterAdapter = HumidityAdapter(this)
        windAdapterAdapter = WindAdapter()

        binding?.let {
            it.recyclerViewWeather.apply {
                this.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = weatherAdapterAdapter
            }

            val humidityLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            it.recyclerViewHumidity.apply {
                this.layoutManager = humidityLayoutManager
                adapter = humidityAdapterAdapter
            }

            it.recyclerViewWind.apply {
                this.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = windAdapterAdapter
            }
        }

        homeViewModel.userLiveHolderWeather.observe(viewLifecycleOwner) {

            weatherAdapterAdapter.setAdapterData(it)
            humidityAdapterAdapter.setAdapterData(it)
            windAdapterAdapter.setAdapterData(it)

            binding?.let { binding ->

                binding.textViewWeatherLabel.text = "오늘"
                binding.textViewWindLabel.text = "오늘"
                binding.textViewHumidityLabel.text = "오늘"

                setLabelChangeEventFromRecyclerView(
                    0,
                    binding.recyclerViewWeather,
                    binding.textViewWeatherLabel,
                    it
                )
                setLabelChangeEventFromRecyclerView(
                    1,
                    binding.recyclerViewHumidity,
                    binding.textViewHumidityLabel,
                    it
                )
                setLabelChangeEventFromRecyclerView(
                    2,
                    binding.recyclerViewWind,
                    binding.textViewWindLabel,
                    it
                )
            }
        }

        homeViewModel.userLiveHolderAir.observe(viewLifecycleOwner) {

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setLabelChangeEventFromRecyclerView(
        code: Int,
        recyclerView: RecyclerView,
        textView: AppCompatTextView,
        data: List<WeatherDTO?>
    ) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

                data[firstPosition]?.let {

                    val today = getCalendarToday()
                    val selTime = getCalendarFromItem(it)

                    val nextLabel = label[if (selTime.timeInMillis > today.timeInMillis) findDiff(selTime, today) else 0]
                    if (textView.text != nextLabel) textView.text = nextLabel
                }
            }
        })
    }

    private fun findDiff(selDay: Calendar, today: Calendar) =
        ((selDay.timeInMillis - today.timeInMillis) / diff).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        @JvmStatic
        @BindingAdapter("bindingSrc")
        fun loadImage(imageView: AppCompatImageView, resId: Int) {
            imageView.setImageResource(resId)
        }
    }

    override fun getItem(position: Int, item: String?) {
        // Log.e("getAdapter", "$position $item")
    }

}

interface GetScrollItemListener {
    fun getItem(position: Int, item: String?)
}