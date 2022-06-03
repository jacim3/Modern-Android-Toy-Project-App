package com.example.walkingpark.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.walkingpark.R
import com.example.walkingpark.constants.*
import com.example.walkingpark.data.model.ResponseCheck
import com.example.walkingpark.data.model.dto.response.AirResponse
import com.example.walkingpark.data.model.dto.response.StationResponse
import com.example.walkingpark.data.model.dto.response.WeatherResponse
import com.example.walkingpark.data.model.dto.simple_panel.*
import com.example.walkingpark.data.model.entity.LocationEntity
import com.example.walkingpark.data.repository.AirApiRepository
import com.example.walkingpark.data.repository.GeocodingRepository
import com.example.walkingpark.data.repository.StationApiRepository
import com.example.walkingpark.data.repository.WeatherApiRepository
import com.example.walkingpark.ui.adapter.home.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.math.abs


const val MINUS = 0
const val PLUS = 1
val WEATHER_TEXT = arrayOf("맑음", "맑음", "맑음", "맑음", "맑음", "맑음")
/*
    TODO 현재는 UI 관련 비즈니스 로직을 작성하지 않았으므로 사용하지 않음.
*/

const val REST_API_RETRY_COUNT = 3

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val weatherRepository: WeatherApiRepository,
    private val airRepository: AirApiRepository,
    private val stationRepository: StationApiRepository,
    private val geocodingRepository: GeocodingRepository
) : AndroidViewModel(application) {

    val userLiveHolderStation = MutableLiveData<StationResponse.Response.Body.Items?>()
    val userLiveHolderAir = MutableLiveData<List<AirResponse.Response.Body.Items>?>()
    val userLiveHolderWeather = MutableLiveData<List<SimplePanelDTO?>>()

    val simpleHomeAddress = MutableLiveData<String>()
    val simplePanelWeather = MutableLiveData<SimplePanel1>()
    val simplePanelAir = MutableLiveData<SimplePanel3>()
    val simpleMinMaxTemperature =
        MutableLiveData<HashMap<String, HashMap<String, String>>>()

    // Api 재시도 횟수 기록. 성공 시 초기화
    private var weatherRetryCount = 0
    private var stationRetryCount = 0
    private var airRetryCount = 0

    var isAirLoaded = MutableLiveData<Int>().apply {
        this.postValue(Common.RESPONSE_INIT)
    }
    var isStationLoaded = MutableLiveData<Int>().apply {
        this.postValue(Common.RESPONSE_INIT)
    }
    var isWeatherLoaded = MutableLiveData<Int>().apply {
        this.postValue(Common.RESPONSE_INIT)
    }

    val userResponseCheck = MediatorLiveData<ResponseCheck>()
        .apply {
            this.addSource(isAirLoaded) {
                this.value = combineResponses(isStationLoaded, isAirLoaded, isWeatherLoaded)
            }
            this.addSource(isStationLoaded) {
                this.value = combineResponses(isStationLoaded, isAirLoaded, isWeatherLoaded)
            }
            this.addSource(isWeatherLoaded) {
                this.value = combineResponses(isStationLoaded, isAirLoaded, isWeatherLoaded)
            }
        }

    fun startGeocodingBeforeStationApi(entity: LocationEntity):
            io.reactivex.rxjava3.disposables.Disposable? =

        geocodingRepository.getAddressSet(entity)
            .retryWhen { error ->
                error.zipWith(
                    Flowable.range(1, REST_API_RETRY_COUNT)
                ) { _, t2 -> t2 }.flatMap {
                    Flowable.timer(it.toLong(), TimeUnit.SECONDS)
                }
            }
            .subscribe(
                {
                    startStationApi(entity, it)
                    Log.e("responseGeoCode", it.toString())
                    simpleHomeAddress.postValue(getCurrentAddress(it))
                },
                {
                    it.printStackTrace()
                }
            )


    private fun getCurrentAddress(addresses: List<String>): String =
        StringBuilder().apply {
            addresses.forEach { address ->
                for (enum in ADDRESS.values()) {
                    if (enum.text == address[address.length - 1] && enum.text != ADDRESS.DO.text) {
                        this.append(address).append(" ")
                        break
                    }
                }
            }
        }.run {
            this.toString()
        }


    private fun startStationApi(entity: LocationEntity, addresses: List<String>) =
        stationRepository.startStationApi(addresses)
            .retryWhen { error ->
                error.zipWith(
                    Flowable.range(1, REST_API_RETRY_COUNT)
                ) { _, t2 -> t2 * 2 }.flatMap {
                    Flowable.timer(it.toLong(), TimeUnit.SECONDS)
                }
            }.subscribeBy(
                onSuccess = { response ->
                    userLiveHolderStation.postValue(
                        getNearestLocation(
                            response.response.body.items,
                            entity
                        )
                    )
                    isStationLoaded.postValue(Common.RESPONSE_SUCCESS)
                },
                onError = {
                    it.printStackTrace()
                    isStationLoaded.postValue(Common.RESPONSE_FAILURE)
                }
            )


    // 여러 측정소 리스트 중 사용자와 가장 가까운 위치 가져오기.
    private fun getNearestLocation(
        items: List<StationResponse.Response.Body.Items>,
        entity: LocationEntity
    ) = items.stream().sorted { p0, p1 ->
        (abs(p0.dmX - entity.latitude) + abs(p0.dmY - entity.longitude))
            .compareTo(
                (abs(p1.dmX - entity.latitude) + abs(p1.dmY - entity.longitude))
            )
    }.collect(Collectors.toList())[0]


    fun startAirApi(stationName: String): Disposable =
        airRepository.startAirApi(stationName)
            .retryWhen { error ->
                error.zipWith(
                    Flowable.range(1, REST_API_RETRY_COUNT)
                ) { _, t2 -> t2 }.flatMap {
                    Flowable.timer(it.toLong(), TimeUnit.SECONDS)
                }
            }
            .subscribeBy(
                onSuccess = { response ->
                    userLiveHolderAir.postValue(response.response.body.items)
                    isAirLoaded.postValue(Common.RESPONSE_SUCCESS)
                    Log.e("AirResponse", "Success")

                    // response 에 의해 받는 리스트 중 맨 처음 항목이 항상 최신
                    val receive = response.response.body.items[0]
                    Log.e(
                        "received Air Data",
                        "${receive.pm10Grade} ${receive.pm25Grade} ${receive.dataTime} ${receive.pm10Value} ${receive.pm25Value}"
                    )
                    simplePanelAir.postValue(parsingSimpleAir(stationName, receive))
                },
                onError = {
                    it.printStackTrace()
                    isAirLoaded.postValue(Common.RESPONSE_FAILURE)
                    Log.e("AirResponse", "Failure")
                }
            )


    // TODO 통신 실패 시, 현재 시간을 기준으로 검색시간을 변경하여 재시도 하도록
    // TODO ResultCode 에 따른 에러핸들링 필요.
    fun startWeatherApi(entity: LocationEntity, calendar: Calendar, calc: Int): Disposable {

/*        .retry { count, error ->
            Timestamp(Calendar.getInstance().apply {
                add(Calendar.HOUR, count * -1)
            }.timeInMillis).time
            count < 3
        }*/

        // 날씨 Api 의 결과는 총 800개가 넘으므로, 이를 약 250개씩 4페이지에 걸쳐 분할하여 받음.
        return Single.zip(
            weatherRepository.startWeatherApi(entity, 1, calendar),
            weatherRepository.startWeatherApi(entity, 2, calendar),
            weatherRepository.startWeatherApi(entity, 3, calendar),
            weatherRepository.startWeatherApi(entity, 4, calendar),
        ) { emit1, emit2, emit3, emit4 ->
            // 응답결과 -> Map 자료구조 변환
            weatherMapToList(
                weatherResponseToMap(
                    weatherResponseCheckAndMerge(
                        listOf(
                            emit1,
                            emit2,
                            emit3,
                            emit4
                        )
                    )
                ), Calendar.getInstance().apply {
                    set(Calendar.YEAR, 1990)
                })
        }
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    if (!it.isNullOrEmpty()) {
                        Log.e("WeatherResponse", "Success")
                        weatherRetryCount = 0
                        isWeatherLoaded.postValue(Common.RESPONSE_SUCCESS)
                        userLiveHolderWeather.postValue(it)
                        it[0]?.let { latestItem ->
                            simplePanelWeather.postValue(parsingSimpleWeather(latestItem))
                        }
                    }
                    // 통신은 성공하였으나, 결과값 없음 -> 실패로 간주 -> 다시시도
                    else {
                        Log.e("WeatherResponse", "Failure")
                        weatherRetryCount++
                        isWeatherLoaded.postValue(Common.RESPONSE_FAILURE)
                        retryWeather(entity, calendar, calc)
                    }
                },
                // 실패하여 이전 1시간을 기준으로 다시 실행. -> 재귀원리
                onError = {
                    Log.e("WeatherResponse", "Failure")
                    weatherRetryCount++
                    isWeatherLoaded.postValue(Common.RESPONSE_FAILURE)
                    retryWeather(entity, calendar, calc)
                }
            )

    }


    // 검색해야 하는 날짜가 현재 시각보다 커질경우, 날짜 연산, Api 통신에 오류가 발생하므로
    // 이를 방지.
    private fun retryWeather(entity: LocationEntity, calendar: Calendar, calc: Int) {
        calendar.add(Calendar.HOUR_OF_DAY, if (calc == PLUS) 1 else -1)
        if (Calendar.getInstance().before(calendar))
            startWeatherApi(entity, getCalendarTodayMin(), MINUS)
        else
            startWeatherApi(entity, calendar, PLUS)
    }

    // SimplePanel 에서 사용할 객체 변환
    private fun parsingSimpleAir(
        stationName: String,
        latestResponse: AirResponse.Response.Body.Items
    ) = SimplePanel3(
        stationName = stationName,
        dust = latestResponse.pm10Value,
        smallDust = latestResponse.pm25Value,
        dustStatus =
        latestResponse.pm10Grade.run {
            try {
                when (this.toInt()) {
                    1 -> "좋음"
                    2 -> "보통"
                    3 -> "나쁨"
                    4 -> "매우나쁨"
                    else -> "정보없음"
                }
            } catch (e: NumberFormatException) {
                "정보없음"
            }
        },
        smallDustStatus =
        latestResponse.pm25Grade.run {
            try {
                when (this.toInt()) {
                    1 -> "좋음"
                    2 -> "보통"
                    3 -> "나쁨"
                    4 -> "매우나쁨"
                    else -> "정보없음"
                }
            } catch (e: NumberFormatException) {
                "정보없음"
            }
        },
        dateTime = latestResponse.dataTime,
        icon =
        latestResponse.pm10Grade.run {
            try {
                when (this.toInt()) {
                    1 -> R.drawable.ic_air_status_good
                    2 -> R.drawable.ic_air_status_normal
                    3 -> R.drawable.ic_air_status_bad
                    4 -> R.drawable.ic_air_status_very_bad
                    else -> R.drawable.ic_air_status_normal
                }
            } catch (e: NumberFormatException) {
                R.drawable.ic_air_status_very_bad
            }
        }
    )

    private fun parsingSimpleWeather(data: SimplePanelDTO) =
        SimplePanel1(
            date = data.date,
            time = data.time,
            windValue = getCalculateWind(data),
            windIcon = R.drawable.ic_wind_direction_arrow,
            humidityValue = data.humidity + "%",
            humidityIcon = getCalculatedHumidityIcon(data.humidity),
            rainChanceValue = data.rainChance + "%",
            rainTypeIcon = getSimplePanelRainTypeIcon(data.rainChance),
            weatherIcon = checkTimeForSetWeatherMenu(data),
            weatherText = getWeatherText(data),
            temperature = data.temperature
        )


    private fun getSimplePanelRainTypeIcon(data: String) =
        data.run {
            try {
                this.toInt()
            } catch (e: NumberFormatException) {
                0
            }
        }.run {
            when (this) {
                1 -> R.drawable.ic_sky_rain
                2 -> R.drawable.ic_sky_snow
                3 -> R.drawable.ic_sky_snow
                4 -> R.drawable.ic_sky_rain
                else -> R.drawable.ic_sky_none
            }
        }


    private fun getCalculateWind(data: SimplePanelDTO) = data.windSpeed.run {
        try {
            val wind = this.toFloat().toInt()
            // 풍속 측정
            when {
                wind < 4 -> {
                    "고요함"
                }
                wind in 4..8 -> {
                    "보통"
                }
                wind in 9..13 -> {
                    "강함"
                }
                wind >= 14 -> {
                    "매우강함"
                }
                else -> {
                    "정보헚음"
                }
            }
        } catch (e: NumberFormatException) {
            "정보없음"
        }
    }

    private fun getWeatherText(data: SimplePanelDTO): String =
        getCalendarFromItem(data).run {

            when (data.rainType.toInt()) {
                RAIN.RAINY.index -> RAIN.RAINY.text
                RAIN.RAIN_SNOW.index -> RAIN.RAIN_SNOW.text
                RAIN.SNOW.index -> RAIN.SNOW.text
                RAIN.SHOWER.index -> RAIN.SHOWER.text

                else -> {
                    when (data.sky.toInt()) {
                        SKY.CLEAR.index -> SKY.CLEAR.text
                        SKY.CLOUDY.index -> SKY.CLOUDY.text
                        SKY.OVERCAST.index -> SKY.OVERCAST.text
                        else -> ""
                    }
                }
            }
        }


    // resultCode 0 은 응답 성공을 의미. 응답에 성공한 객체만 합쳐서 출력.
    private fun weatherResponseCheckAndMerge(responses: List<WeatherResponse>): List<WeatherResponse.Response.Body.Items.Item> {
        return listOf<WeatherResponse.Response.Body.Items.Item>().toMutableList()
            .apply {
                responses.forEach {
                    if (it.response.header.resultCode == 0)
                        this += it.response.body.items.item
                }
            }

    }

    // Api 에서 데이터를 category 로 구분하여 분할하여 보내주므로, 이를 효율적으로 이용하기 위하여
    // outerKey : Date, innerKey : Time, innerValue : Values 의 3 Level Map 으로 변환
    private fun weatherResponseToMap(responses: List<WeatherResponse.Response.Body.Items.Item>):
            Map<String, Map<String, Map<String, String>>> {
        return responses
            .groupBy {
                it.fcstDate
            }.mapValues { outer ->
                outer.value.groupBy {
                    it.fcstTime
                }.mapValues { inner ->
                    inner.value.associate {
                        it.category to it.fcstValue
                    }
                }
            }

    }

    // 위에서 통합한 3-Level map 객체를 recyclerView 에서 사용하기 위한 List<WeatherDTO> 변환
    private fun weatherMapToList(
        map: Map<String, Map<String, Map<String, String>>>,
        prevDate: Calendar
    ): List<SimplePanelDTO?> {
        return emptyList<SimplePanelDTO?>().toMutableList()
            .apply {
                map.forEach { outer ->
                    outer.value.forEach { inner ->
                        getCalendarFromYYYYMMDDHHmm(outer.key + inner.key).let { target ->
                            getCalendarTodayCurrentHour().let { current ->

                                setMinMaxTemperature(outer, inner, "max")
                                setMinMaxTemperature(outer, inner, "min")

                                // 현재 시각을 기준으로 이전시간 데이터는 걸러내기.
                                if (abs(current.timeInMillis - target.timeInMillis) < 100
                                    || current.before(target)
                                ) {
                                    if (prevDate.get(Calendar.YEAR) != 1990) {
                                        if (
                                            prevDate.get(Calendar.YEAR) != target.get(
                                                Calendar.YEAR
                                            ) ||
                                            prevDate.get(Calendar.MONTH) != target.get(
                                                Calendar.MONTH
                                            ) ||
                                            prevDate.get(Calendar.DAY_OF_MONTH) != target.get(
                                                Calendar.DAY_OF_MONTH
                                            )
                                        ) {
                                            this.add(null)
                                        }
                                    }

                                    prevDate.set(
                                        target.get(Calendar.YEAR),
                                        target.get(Calendar.MONTH),
                                        target.get(Calendar.DAY_OF_MONTH)
                                    )

                                    this.add(setSimplePanelDTO(outer, inner))
                                }
                            }
                        }
                    }
                }
            }

    }

    private fun setSimplePanelDTO(
        outer: Map.Entry<String, Map<String, Map<String, String>>>,
        inner: Map.Entry<String, Map<String, String>>,
    ) =
        SimplePanelDTO(
            date = outer.key,
            time = inner.key,
            temperature = inner.value[WEATHER.TEMPERATURE.code]
                ?: Common.NO_DATA,
            humidity = inner.value[WEATHER.HUMIDITY.code]
                ?: Common.NO_DATA,
            rainChance = inner.value[WEATHER.RAIN_CHANCE.code]
                ?: Common.NO_DATA,
            rainType = inner.value[WEATHER.RAIN_TYPE.code]
                ?: Common.NO_DATA,
            snow = inner.value[WEATHER.SNOW.code] ?: Common.NO_DATA,
            windSpeed = inner.value[WEATHER.WIND_SPEED.code]
                ?: Common.NO_DATA,
            windEW = inner.value[WEATHER.WIND_SPEED_EW.code]
                ?: Common.NO_DATA,
            windNS = inner.value[WEATHER.WIND_SPEED_NS.code]
                ?: Common.NO_DATA,
            sky = inner.value[WEATHER.SKY.code] ?: Common.NO_DATA,
        )

    private fun setMinMaxTemperature(
        outer: Map.Entry<String, Map<String, Map<String, String>>>,
        inner: Map.Entry<String, Map<String, String>>,
        tag: String,
    ) {
        (if (tag == "max") WEATHER.TEMPERATURE_HIGH.code else WEATHER.TEMPERATURE_LOW.code)
            .run {
                inner.value[this]?.let { value ->
                    outer.key.substring(0, 8).let { date ->

                        simpleMinMaxTemperature.let { liveData ->
                            liveData.value?.let { map ->
                                map[date]?.let {
                                    it[tag] = value
                                } ?: let {
                                    map[date] = hashMapOf(
                                        tag to value
                                    )
                                }
                            } ?: run {
                                simpleMinMaxTemperature.setValue(
                                    setMinMaxTemperatureInnerMap(date, tag, value)
                                )
                            }
                        }
                    }
                }
            }

    }

    private fun setMinMaxTemperatureInnerMap(date: String, tag: String, value: String) =
        hashMapOf(
            date to hashMapOf(
                tag to value
            )
        )

    private fun combineResponses(
        station: MutableLiveData<Int>?,
        air: MutableLiveData<Int>?,
        weather: MutableLiveData<Int>?
    ): ResponseCheck {
        return ResponseCheck(
            station = station?.value ?: Common.RESPONSE_INIT,
            air = air?.value ?: Common.RESPONSE_INIT,
            weather = weather?.value ?: Common.RESPONSE_INIT
        )
    }

    private fun combineMinMaxTemperature(
        min: MutableLiveData<String>,
        max: MutableLiveData<String>
    ): SimplePanel2 {
        return SimplePanel2(min.value ?: "", max.value ?: "")
    }
}

fun returnAmPmAfterCheck(hoursOfDay: Int, hour: Int) =
    "${if (hoursOfDay < 12) "오전 " else "오후"} ${if (hour == 0) 12 else hour}시"


// TODO Calendar 객체는 Month 가 0 부터 시작 (0~11) 이를 감안하여 처리해야 한다.
fun getCalendarFromYYYYMMDDHHmm(item: String): Calendar =

    Calendar.getInstance().apply {
        set(
            item.substring(0, 4).toInt(),
            item.substring(4, 6).toInt() - 1,
            item.substring(6, 8).toInt(),
            item.substring(8, 10).toInt(),
            0,
            0
        )
    }


// TODO Calendar 객체는 Month 가 0 부터 시작 (0~11) 이를 감안하여 처리해야 한다.
fun getCalendarFromItem(item: SimplePanelDTO): Calendar =
    (item.date + item.time).run {
        Calendar.getInstance().apply {
            set(
                this@run.substring(0, 4).toInt(),
                this@run.substring(4, 6).toInt() - 1,
                this@run.substring(6, 8).toInt(),
                this@run.substring(8, 10).toInt(),
                this@run.substring(10).toInt(),
                0
            )
        }
    }

// Calendar 의 차이에 따른 날짜의 갯수를 구해야 하므로, 해당 날짜의 최소시작을 리턴
fun getCalendarTodayMin(): Calendar = Calendar.getInstance().apply {
    set(
        this.get(Calendar.YEAR),
        this.get(Calendar.MONTH),
        this.get(Calendar.DAY_OF_MONTH),
        0,
        0,
        0
    )
}

fun getCalendarTodayCurrentHour(): Calendar = Calendar.getInstance().apply {
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
}






