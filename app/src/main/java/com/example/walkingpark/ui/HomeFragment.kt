package com.example.walkingpark.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.walkingpark.constants.Common
import com.example.walkingpark.data.model.dto.simple_panel.SimplePanelDTO
import com.example.walkingpark.databinding.FragmentHomeBinding
import com.example.walkingpark.ui.adapter.home.*
import com.example.walkingpark.ui.view.LoadingIndicator
import com.example.walkingpark.ui.viewmodels.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

const val diff = 24 * 60 * 60 * 1000       // calendar 간 날짜계산을 위해 필요
val week = arrayOf("일", "월", "화", "수", "목", "금", "토")
val label = arrayOf("오늘", "내일", "모레", "글피")     // 레이블 텍스트

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
                    this.value != Common.RESPONSE_SUCCESS &&
                    this.value == Common.RESPONSE_INIT
                ) {
                    this.postValue(Common.RESPONSE_PROCEEDING)
                    Calendar.getInstance().apply {
                    }
                    homeViewModel.startWeatherApi(it, getCalendarTodayMin(), PLUS)
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

    private fun setAdapters() {
        weatherAdapterAdapter = WeatherAdapter()
        humidityAdapterAdapter = HumidityAdapter()
        windAdapterAdapter = WindAdapter()

        var decorator: RecyclerView.ItemDecoration? = null

        binding?.let {
            it.recyclerViewWeather.apply {
                this.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = weatherAdapterAdapter
                LinearSnapHelper().attachToRecyclerView(this)
            }

            val humidityLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            it.recyclerViewHumidity.apply {
                this.layoutManager = humidityLayoutManager
                adapter = humidityAdapterAdapter
                LinearSnapHelper().attachToRecyclerView(this)
            }

            it.recyclerViewWind.apply {
                this.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = windAdapterAdapter
                LinearSnapHelper().attachToRecyclerView(this)
            }
        }

        homeViewModel.userLiveHolderWeather.observe(viewLifecycleOwner) {

            weatherAdapterAdapter.setAdapterData(it)
            humidityAdapterAdapter.setAdapterData(it)
            windAdapterAdapter.setAdapterData(it)

            binding?.let { binding ->

                val today = getCalendarTodayMin()
                binding.textViewWeatherLabel.text = setLabel(0, today)
                binding.textViewWindLabel.text = setLabel(0, today)
                binding.textViewHumidityLabel.text = setLabel(0, today)


                setLabelChangeEventFromRecyclerView(
                    binding.recyclerViewWeather,
                    binding.textViewWeatherLabel,
                    it,
                    today
                )
                setLabelChangeEventFromRecyclerView(
                    binding.recyclerViewHumidity,
                    binding.textViewHumidityLabel,
                    it,
                    today
                )
                setLabelChangeEventFromRecyclerView(
                    binding.recyclerViewWind,
                    binding.textViewWindLabel,
                    it,
                    today
                )
            }
        }
        homeViewModel.userLiveHolderAir.observe(viewLifecycleOwner) {
        }

        homeViewModel.simpleMinMaxTemperature.observe(viewLifecycleOwner) {
            Log.e("MinMaxObserver", it.toString())
        }
    }

    private fun setLabelChangeEventFromRecyclerView(
        recyclerView: RecyclerView,
        textView: AppCompatTextView,
        data: List<SimplePanelDTO?>,
        today: Calendar
    ) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                ((recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()).let { firstPosition ->
                    data[firstPosition]?.let {
                        val nextLabel = setLabel(
                            findDiff(
                                getCalendarFromItem(it),
                                today
                            ).run {
                                when {
                                    this < 0 -> 0
                                    this > label.size - 1 -> label.size - 1
                                    else -> this
                                }
                            },
                            getCalendarFromItem(it)
                        )
                        if (textView.text != nextLabel) textView.text = nextLabel
                    }
                }
            }
        })
    }

    private fun setLabel(index: Int, time: Calendar) =
        "${label[index]}{${week[time.get(Calendar.DAY_OF_WEEK) - 1]})"

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

        @JvmStatic
        @BindingAdapter("bindingHashMap", "check")
        fun loadText(
            textView: AppCompatTextView,
            item: MutableLiveData<HashMap<String, HashMap<String, String>>>,
            check: Int
        ) {
            item.value?.get(Common.DateFormat.format(Date()))?.let {
                textView.text = it[if (check == 1) "max" else "min"]
            }
        }
    }
}