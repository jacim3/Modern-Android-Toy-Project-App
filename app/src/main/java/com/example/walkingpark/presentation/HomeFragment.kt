package com.example.walkingpark.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.walkingpark.R
import com.example.walkingpark.constants.Common
import com.example.walkingpark.databinding.FragmentHomeBinding
import com.example.walkingpark.presentation.view.LoadingIndicator
import com.example.walkingpark.presentation.viewmodels.HomeViewModel
import com.example.walkingpark.presentation.viewmodels.MainViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.common.net.HttpHeaders.RANGE
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
        setChart()
        setData(5 , 5.0f)
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
                    it.air != Common.RESPONSE_SUCCESS) {
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
    }

    fun setChart(){
        val chart = binding?.lineChartAir
        chart?.let {
            chart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 10f
                textColor = android.graphics.Color.RED
                setDrawAxisLine(true)
                setDrawGridLines(false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun configureChartAppearance(lineChart: LineChart, range: Int) {
        lineChart.extraBottomOffset = 15f // 간격
        lineChart.description.isEnabled = false // chart 밑에 description 표시 유무

        // Legend는 차트의 범례
        val legend = lineChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.form = Legend.LegendForm.CIRCLE
        legend.formSize = 10f
        legend.textSize = 13f
        legend.textColor = Color.parseColor("#A3A3A3")
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.yEntrySpace = 5f
        legend.isWordWrapEnabled = true
        legend.xOffset = 80f
        legend.yOffset = 20f
        legend.calculatedLineSizes

        // XAxis (아래쪽) - 선 유무, 사이즈, 색상, 축 위치 설정
        val xAxis = lineChart.xAxis
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM // x축 데이터 표시 위치
        xAxis.granularity = 1f
        xAxis.textSize = 14f
        xAxis.textColor = R.color.black
        xAxis.spaceMin = 0.1f // Chart 맨 왼쪽 간격 띄우기
        xAxis.spaceMax = 0.1f // Chart 맨 오른쪽 간격 띄우기

        // YAxis(Right) (왼쪽) - 선 유무, 데이터 최솟값/최댓값, 색상
        val yAxisLeft = lineChart.axisLeft
        yAxisLeft.textSize = 14f
        yAxisLeft.textColor = R.color.black
        yAxisLeft.setDrawAxisLine(false)
        yAxisLeft.axisLineWidth = 2f
        yAxisLeft.axisMinimum = 0f // 최솟값
        yAxisLeft.axisMaximum = RANGE.get(0).get(range) // 최댓값
        yAxisLeft.granularity = RANGE.get(1).get(range)

        // YAxis(Left) (오른쪽) - 선 유무, 데이터 최솟값/최댓값, 색상
        val yAxis = lineChart.axisRight
        yAxis.setDrawLabels(false) // label 삭제
        yAxis.textColor = R.color.black
        yAxis.setDrawAxisLine(false)
        yAxis.axisLineWidth = 2f
        yAxis.axisMinimum = 0f // 최솟값
        yAxis.axisMaximum = RANGE.get(0).get(range) // 최댓값
        yAxis.granularity = RANGE.get(1).get(range)

        // XAxis에 원하는 String 설정하기 (날짜)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return LABEL.get(range).get(value.toInt())
            }
        }
    }

    private fun createChartData(range: Int): LineData? {
        val entry1: ArrayList<Map.Entry<*, *>> = ArrayList() // 앱1
        val entry2: ArrayList<Map.Entry<*, *>> = ArrayList() // 앱2
        val entry3: ArrayList<Map.Entry<*, *>> = ArrayList() // 앱3
        val entry4: ArrayList<Map.Entry<*, *>> = ArrayList() // 앱4
        val chartData = LineData()

        // 랜덤 데이터 추출
        for (i in 0..3) {
            val val1 = (Math.random() * RANGE[0].get(range)) as Float // 앱1 값
            val val2 = (Math.random() * RANGE[0].get(range)) as Float // 앱2 값
            val val3 = (Math.random() * RANGE[0].get(range)) as Float // 앱3 값
            val val4 = (Math.random() * RANGE[0].get(range)) as Float // 앱4 값
            entry1.add(MutableMap.MutableEntry<Any?, Any?>(i, val1))
            entry2.add(MutableMap.MutableEntry<Any?, Any?>(i, val2))
            entry3.add(MutableMap.MutableEntry<Any?, Any?>(i, val3))
            entry4.add(MutableMap.MutableEntry<Any?, Any?>(i, val4))
        }

        // 4개 앱의 DataSet 추가 및 선 커스텀

        // 앱1
        val lineDataSet1 = LineDataSet(entry1, APPS.get(0))
        chartData.addDataSet(lineDataSet1)
        lineDataSet1.lineWidth = 3f
        lineDataSet1.circleRadius = 6f
        lineDataSet1.setDrawValues(false)
        lineDataSet1.setDrawCircleHole(true)
        lineDataSet1.setDrawCircles(true)
        lineDataSet1.setDrawHorizontalHighlightIndicator(false)
        lineDataSet1.setDrawHighlightIndicators(false)
        lineDataSet1.color = Color.rgb(255, 155, 155)
        lineDataSet1.setCircleColor(Color.rgb(255, 155, 155))

        // 앱2
        val lineDataSet2 = LineDataSet(entry2, APPS.get(1))
        chartData.addDataSet(lineDataSet2)
        lineDataSet2.lineWidth = 3f
        lineDataSet2.circleRadius = 6f
        lineDataSet2.setDrawValues(false)
        lineDataSet2.setDrawCircleHole(true)
        lineDataSet2.setDrawCircles(true)
        lineDataSet2.setDrawHorizontalHighlightIndicator(false)
        lineDataSet2.setDrawHighlightIndicators(false)
        lineDataSet2.color = Color.rgb(178, 223, 138)
        lineDataSet2.setCircleColor(Color.rgb(178, 223, 138))

        // 앱3
        val lineDataSet3 = LineDataSet(entry3, APPS.get(2))
        chartData.addDataSet(lineDataSet3)
        lineDataSet3.lineWidth = 3f
        lineDataSet3.circleRadius = 6f
        lineDataSet3.setDrawValues(false)
        lineDataSet3.setDrawCircleHole(true)
        lineDataSet3.setDrawCircles(true)
        lineDataSet3.setDrawHorizontalHighlightIndicator(false)
        lineDataSet3.setDrawHighlightIndicators(false)
        lineDataSet3.color = Color.rgb(166, 208, 227)
        lineDataSet3.setCircleColor(Color.rgb(166, 208, 227))

        // 앱4
        val lineDataSet4 = LineDataSet(entry4, APPS.get(3))
        chartData.addDataSet(lineDataSet4)
        lineDataSet4.lineWidth = 3f
        lineDataSet4.circleRadius = 6f
        lineDataSet4.setDrawValues(false)
        lineDataSet4.setDrawCircleHole(true)
        lineDataSet4.setDrawCircles(true)
        lineDataSet4.setDrawHorizontalHighlightIndicator(false)
        lineDataSet4.setDrawHighlightIndicators(false)
        lineDataSet4.color = Color.rgb(31, 120, 180)
        lineDataSet4.setCircleColor(Color.rgb(31, 120, 180))
        chartData.setValueTextSize(15f)
        return chartData
    }


}