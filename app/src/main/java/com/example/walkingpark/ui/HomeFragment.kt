package com.example.walkingpark.ui

import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.TranslateAnimation
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.NestedScrollView
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
import com.example.walkingpark.ui.viewmodels.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

const val diff = 24 * 60 * 60 * 1000       // calendar 간 날짜계산을 위해 필요
val week = arrayOf("일", "월", "화", "수", "목", "금", "토")
val label = arrayOf("오늘", "내일", "모레", "글피")     // 레이블 텍스트
val START = 0
val END = 1

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

        // 사용자 GPS 정보 획득 시 수행.
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

        homeViewModel.userResponseCheck.observe(viewLifecycleOwner) { check ->

            if (check.station == Common.RESPONSE_SUCCESS &&
                check.air != Common.RESPONSE_PROCEEDING &&
                check.air != Common.RESPONSE_SUCCESS
            ) {
                homeViewModel.userLiveHolderStation.value?.stationName?.let { name ->
                    homeViewModel.isAirLoaded.postValue(Common.RESPONSE_PROCEEDING)
                    homeViewModel.startAirApi(name)
                }
            }

            if (check.air == Common.RESPONSE_SUCCESS &&
                check.weather == Common.RESPONSE_SUCCESS
            ) {
                binding?.let {
                    it.loadingIndicator.visibility = View.INVISIBLE
                    it.mainContents.visibility = View.VISIBLE
                }
            }
        }

        homeViewModel.isDustPanelAnimationStart.observe(viewLifecycleOwner) { check ->
            if (check == true) {
                setDustAnimationWithCalculatePosition()
            }
        }
        setButtonTabEvent()
        setAdapters()
        setScrollEvent()
    }

    private fun setButtonTabEvent() {
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

    // 미세먼지 값 -> 디바이스의 width 및 각 Widget 의 위치를 고려한 Position 변환 및
    private fun setDustAnimationWithCalculatePosition() {
        binding?.let {

            homeViewModel.detailPanelDust.value?.also { data ->
                // 이하 디바이스의 최대 width, 뷰 정보, 위치등을 읽어옴.
                val l1 = it.includeDustPanel.fineDust1      // 범위 - 매우좋음
                val l2 = it.includeDustPanel.fineDust2      // 범위 - 좋음
                val l3 = it.includeDustPanel.fineDust3      // 범위 - 보통
                val l4 = it.includeDustPanel.fineDust4      // 범위 - 나쁨
                val l5 = it.includeDustPanel.fineDust5      // 범위 - 매우나쁨

                val halfWidth = it.includeDustPanel.indicatorInnerContainer.run {
                    it.includeDustPanel.indicatorInnerContainer.width.toFloat() / 2
                }
                val maxWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()

                // 각 영역의 범위
                val range = arrayOf(
                    arrayOf(l1.x + l1.x, l2.x - l1.x),
                    arrayOf(l2.x + l1.x, l3.x - l1.x),
                    arrayOf(l3.x + l1.x, l4.x - l1.x),
                    arrayOf(l4.x + l1.x, l5.x - l1.x),
                    arrayOf(
                        l5.x + l1.x,
                        maxWidth - l1.x
                    ),
                )

                Handler(Looper.getMainLooper()).post {
                    it.includeDustPanel.let { panel ->
                        panel.IndicatorContainerOuter.startAnimation(
                            getDustAnimationWithData(
                                0,
                                data[0].value.toInt(),
                                range,
                                halfWidth,
                                maxWidth
                            )
                        )
                    }
                }

                Handler(Looper.getMainLooper()).post {
                    it.includeUltraDustPanel.let { panel ->
                        panel.IndicatorContainerOuter.startAnimation(
                            getDustAnimationWithData(
                                1,
                                data[1].value.toInt(),
                                range,
                                halfWidth,
                                maxWidth
                            )
                        )
                    }
                }
            }
        }
    }

    // 미세먼지 값 -> Position (Pixel) 변환
    private fun dustCheck(
        value: Int,
        range: Array<Array<Float>>,
        indicatorHalfWidth: Float,
        maxWidth: Float
    ) = when (value) {
        in 0..15 -> dustValueToPosition(value, range[0], 0 to 15).run {
            if (this < indicatorHalfWidth) indicatorHalfWidth else this
        }
        in 16..30 -> dustValueToPosition(value, range[1], 16 to 30)
        in 31..80 -> dustValueToPosition(value, range[2], 31 to 80)
        in 81..150 -> dustValueToPosition(value, range[3], 81 to 150)
        else -> {
            dustValueToPosition(value, range[4], 150 to 200).run {
                if (this > maxWidth - indicatorHalfWidth) maxWidth - indicatorHalfWidth else this
            }
        }
    }

    // 초미세먼지 값 -> Position (Pixel) 변환
    private fun ultraDustCheck(
        value: Int,
        range: Array<Array<Float>>,
        indicatorHalfWidth: Float,
        maxWidth: Float
    ) = when (value) {
        in 0..7 -> dustValueToPosition(value, range[0], 0 to 7).run {
            if (this < indicatorHalfWidth) indicatorHalfWidth else this
        }
        in 8..15 -> dustValueToPosition(value, range[1], 8 to 15)
        in 16..35 -> dustValueToPosition(value, range[2], 16 to 35)
        in 36..75 -> dustValueToPosition(value, range[3], 36 to 75)
        else -> {
            dustValueToPosition(value, range[4], 76 to 100).run {
                if (this > maxWidth - indicatorHalfWidth) maxWidth - indicatorHalfWidth else this
            }
        }
    }

    private fun dustValueToPosition(
        value: Int,
        area: Array<Float>,
        range: Pair<Int, Int>
    ) = (range.second - range.first).run {
        area[START] + ((value - range.first) * (area[END] - area[START]) / this)
    }

    private fun setScrollEvent() {
        binding?.let {
            it.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

                if ((it.includeUltraDustPanel.container.y - it.includeUltraDustPanel.container.height) <= scrollY && homeViewModel.isDustPanelAnimationStart.value == false) {
                    homeViewModel.isDustPanelAnimationStart.postValue(true)
                }
            })
        }
    }

    // 리턴받은 Position (Value 에 의하여 변환) 값을 Animation 객체 생성 및 리턴.
    private fun getDustAnimationWithData(
        code: Int,
        value: Int,
        range: Array<Array<Float>>,
        halfWidth: Float,
        maxWidth: Float
    ) = TranslateAnimation(
        0f,
        if (code == 0) dustCheck(value, range, halfWidth, maxWidth)
        else ultraDustCheck(value, range, halfWidth, maxWidth),
        0f,
        0f
    ).apply {
        duration = 2000
        interpolator = AccelerateDecelerateInterpolator()
        fillAfter = true
    }

    // 리사이클러뷰 초기화 및 어댑터 등록.
    private fun setAdapters() {
        weatherAdapterAdapter = WeatherAdapter()
        humidityAdapterAdapter = HumidityAdapter()
        windAdapterAdapter = WindAdapter()

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
    }


    // RecyclerView 스크롤 이벤트에 따라 날짜 Label 변경 이벤트
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
                            findDiffDay(
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

    // 차이 일수를 label 텍스트로 변환
    private fun setLabel(index: Int, time: Calendar) =
        "${label[index]}{${week[time.get(Calendar.DAY_OF_WEEK) - 1]})"

    // 두 Calendar 객체를 통하여 차이 구하여 리턴 : Int
    private fun findDiffDay(selDay: Calendar, today: Calendar) =
        ((selDay.timeInMillis - today.timeInMillis) / diff).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {

        // 이미지 바인딩
        @JvmStatic
        @BindingAdapter("bindingSrc")
        fun loadImage(imageView: AppCompatImageView, resId: Int) {
            imageView.setImageResource(resId)
        }

        @JvmStatic
        @BindingAdapter("bindingBackground")
        fun loadBackground(container: LinearLayoutCompat, resId: Int) {
            container.setBackgroundResource(resId)
        }

        // 하루 최저 / 최고온도 바인딩
        // check : 0 - 최저온도, 1 - 최고온도
        // day : 오눌 - 0, 내일 - 1, 모레 - 2
        @JvmStatic
        @BindingAdapter("bindingHashMap", "minMax", "baseDate")
        fun loadText(
            textView: AppCompatTextView,
            item: MutableLiveData<HashMap<String, HashMap<String, String>>>,
            minMax: String,
            baseDate: String
        ) {
            item.value?.get(
                Common.DateFormat.format(
                    Calendar.getInstance().apply {
                        this.add(Calendar.DAY_OF_MONTH, baseDate.toInt())
                    }.time
                )
            )?.let { textView.text = it[if (minMax == "최대온도") "max" else "min"] }
            ?: let { textView.text = "정보 없음" }
        }

        // 미세먼지 컨데이너 색 설정
        // check : 0 - 미세먼지, 1 - 초미세먼지
        @JvmStatic
        @BindingAdapter("bindingDustSetColor", "category")
        fun setColor(view: AppCompatImageView, value: String, label: String) {
            Log.e("asdfasdfdas", view.javaClass.name)
        }
    }
}