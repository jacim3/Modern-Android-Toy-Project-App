package com.example.walkingpark.presentation.viewmodels

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.walkingpark.R
import com.example.walkingpark.constants.Common
import com.example.walkingpark.constants.WEATHER
import com.example.walkingpark.data.model.ResponseCheck
import com.example.walkingpark.data.model.dto.SimpleAir
import com.example.walkingpark.data.model.dto.SimpleWeather
import com.example.walkingpark.data.model.dto.WeatherDTO
import com.example.walkingpark.data.model.dto.response.AirResponse
import com.example.walkingpark.data.model.dto.response.StationResponse
import com.example.walkingpark.data.model.dto.response.WeatherResponse
import com.example.walkingpark.data.model.entity.LocationEntity
import com.example.walkingpark.data.repository.AirApiRepository
import com.example.walkingpark.data.repository.GeocodingRepository
import com.example.walkingpark.data.repository.StationApiRepository
import com.example.walkingpark.data.repository.WeatherApiRepository
import com.example.walkingpark.presentation.adapter.home.getCalendarFromItem
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import javax.inject.Inject
import kotlin.math.abs


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
    val userLiveHolderWeather = MutableLiveData<List<WeatherDTO?>>()

    val userSimplePanelAir = MutableLiveData<SimpleAir>()
    val userSimplePanelWeather = MutableLiveData<SimpleWeather>()

    var isAirLoaded = MutableLiveData<Int>()
    var isStationLoaded = MutableLiveData<Int>()
    var isWeatherLoaded = MutableLiveData<Int>()

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

    fun startGeocodingBeforeStationApi(entity: LocationEntity): io.reactivex.rxjava3.disposables.Disposable {
        return geocodingRepository.getAddressSet(entity)
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
                },
                {
                    it.printStackTrace()
                }
            )

    }

    private fun startStationApi(entity: LocationEntity, addresses: List<String>): Disposable {

        return stationRepository.startStationApi(addresses)
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

    }

    // 여러 측정소 리스트 중 사용자와 가장 가까운 위치 가져오기.
    private fun getNearestLocation(
        items: List<StationResponse.Response.Body.Items>,
        entity: LocationEntity
    ): StationResponse.Response.Body.Items? {

        return items.stream().sorted { p0, p1 ->
            (abs(p0.dmX - entity.latitude) + abs(p0.dmY - entity.longitude))
                .compareTo(
                    (abs(p1.dmX - entity.latitude) + abs(p1.dmY - entity.longitude))
                )
        }.collect(Collectors.toList())[0]
    }

    // TODO PM25(초미세먼지) 관련 값을 항상 NULL 로 받아오는 문제 발견. -> 다른곳에는 문제 없음
    fun startAirApi(stationName: String): Disposable {
        return airRepository.startAirApi(stationName)
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
                    userSimplePanelAir.postValue(parsingSimpleAir(stationName, receive))
                },
                onError = {
                    it.printStackTrace()
                    isAirLoaded.postValue(Common.RESPONSE_FAILURE)
                    Log.e("AirResponse", "Failure")
                }
            )

    }

    // TODO 통신 실패 시, 현재 시간을 기준으로 검색시간을 변경하여 재시도 하도록
    // TODO ResultCode 에 따른 에러핸들링 필요.
    @RequiresApi(Build.VERSION_CODES.O)
    fun startWeatherApi(entity: LocationEntity): Disposable {

/*        .retry { count, error ->
            Timestamp(Calendar.getInstance().apply {
                add(Calendar.HOUR, count * -1)
            }.timeInMillis).time
            count < 3
        }*/

        // 날씨 Api 의 결과는 총 800개가 넘으므로, 이를 약 250개씩 4페이지에 걸쳐 분할하여 받음.
        return Single.zip(
            weatherRepository.startWeatherApi(entity, 1),
            weatherRepository.startWeatherApi(entity, 2),
            weatherRepository.startWeatherApi(entity, 3),
            weatherRepository.startWeatherApi(entity, 4),
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
                )
            )
        }.subscribeBy(
            onSuccess = {
                isWeatherLoaded.postValue(Common.RESPONSE_SUCCESS)

                val prevDate = Calendar.getInstance().apply { set(1990, 1, 1) }
                insertSeperatorAndUpdateWeatherLiveData(it.toMutableList(), prevDate)

                userSimplePanelWeather.postValue(parsingSimpleWeather(it[0]))
                Log.e("asdfasdfasdfasd", "${it[0].windSpeed}    asfdafsad")
                Log.e("WeatherResponse", "Success")
            },
            onError = {
                isWeatherLoaded.postValue(Common.RESPONSE_FAILURE)
                it.printStackTrace()
                Log.e("WeatherResponse", "Failure")
            }
        )
    }

    // Seperator Flag 로 사용할 null 데이터 삽입을 수행하는 메서드.
    // ConcurrentException 으로 인하여 Reactive 사용.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertSeperatorAndUpdateWeatherLiveData(
        tmp: MutableList<WeatherDTO?>,
        prevDate: Calendar,
    ) {
        var count = 0   // 원본 배열의 변경사항이 Rx Observable 간 위치값 이슈를 해결하기 위한 index 가충치 변수
        val observable = Observable.fromIterable(tmp)
            .map { s -> Indexed(tmp.indexOf(s), s) }
            .observeOn(Schedulers.io())
            .subscribeBy(
                onNext = {
                    val dateTime = getCalendarFromItem(it.item)
                    if (prevDate.get(Calendar.YEAR) != 1990) {

                        if (
                            prevDate.get(Calendar.YEAR) != dateTime.get(Calendar.YEAR) ||
                            prevDate.get(Calendar.MONTH) != dateTime.get(Calendar.MONTH) ||
                            prevDate.get(Calendar.DAY_OF_MONTH) != dateTime.get(Calendar.DAY_OF_MONTH)
                        ) {
                            tmp.add(
                                it.index + count++,
                                null
                            )
                        }
                    }
                    prevDate.set(
                        dateTime.get(Calendar.YEAR),
                        dateTime.get(Calendar.MONTH),
                        dateTime.get(Calendar.DAY_OF_MONTH)
                    )
                },
                onComplete = {
                    userLiveHolderWeather.postValue(tmp)
                },
                onError = {}
            )

    }


    // SimplePanel 에서 사용할 객체 변환
    private fun parsingSimpleAir(
        stationName: String,
        latestResponse: AirResponse.Response.Body.Items
    ) = SimpleAir(
        stationName = stationName,
        dust = latestResponse.pm10Value ?: Common.NO_DATA,
        smallDust = latestResponse.pm25Value ?: Common.NO_DATA,
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
        dateTime = latestResponse.dataTime ?: Common.NO_DATA,
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

    private fun parsingSimpleWeather(data: WeatherDTO) =

        SimpleWeather(
            date = data.date ?: Common.NO_DATA,
            time = data.time ?: Common.NO_DATA,
            windValue = data.windSpeed.run {
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
            },
            windIcon = R.drawable.ic_weather_wind_arrow,
            humidityValue = data.humidity + "%" ?: "정보없음",
            humidityIcon = R.drawable.ic_weather_humidity,
            rainChanceValue = data.rainChance + "%" ?: "0",
            rainChanceIcon = data.rainType.run {
                try {
                    when (this.toInt()) {
                        0 -> R.drawable.ic_weather_rain     // 없음
                        1 -> R.drawable.ic_weather_rain     // 비
                        2 -> R.drawable.ic_weather_rain     // 비/눈
                        3 -> R.drawable.ic_weather_rain     // 눈
                        4 -> R.drawable.ic_weather_rain     // 소나기
                        else -> R.drawable.ic_weather_rain
                    }
                } catch (e: NumberFormatException) {
                    R.drawable.ic_weather_rain
                }
            }
        )

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
    private fun weatherMapToList(map: Map<String, Map<String, Map<String, String>>>): List<WeatherDTO> {
        return emptyList<WeatherDTO>().toMutableList().apply {
            map.forEach { outer ->
                outer.value.forEach {
                    this.add(
                        WeatherDTO(
                            date = outer.key,
                            time = it.key,
                            temperature = it.value[WEATHER.TEMPERATURE.code] ?: Common.NO_DATA,
                            temperatureMax = it.value[WEATHER.TEMPERATURE_HIGH.code]
                                ?: Common.NO_DATA,
                            temperatureMin = it.value[WEATHER.TEMPERATURE_LOW.code]
                                ?: Common.NO_DATA,
                            humidity = it.value[WEATHER.HUMIDITY.code] ?: Common.NO_DATA,
                            rainChance = it.value[WEATHER.RAIN_CHANCE.code] ?: Common.NO_DATA,
                            rainType = it.value[WEATHER.RAIN_TYPE.code] ?: Common.NO_DATA,
                            snow = it.value[WEATHER.SNOW.code] ?: Common.NO_DATA,
                            windSpeed = it.value[WEATHER.WIND_SPEED.code] ?: Common.NO_DATA,
                            windEW = it.value[WEATHER.WIND_SPEED_EW.code] ?: Common.NO_DATA,
                            windNS = it.value[WEATHER.WIND_SPEED_NS.code] ?: Common.NO_DATA,
                            sky = it.value[WEATHER.SKY.code] ?: Common.NO_DATA,
                        )
                    )
                }
            }
        }
    }

    private fun combineResponses(
        station: MutableLiveData<Int>?,
        air: MutableLiveData<Int>?,
        weather: MutableLiveData<Int>?
    ): ResponseCheck {
        return ResponseCheck(
            air = Common.RESPONSE_FAILURE,
            station = Common.RESPONSE_FAILURE,
            weather = Common.RESPONSE_FAILURE
        ).apply {
            station?.value?.let {
                this.station = it
            }
            air?.value?.let {
                this.air = it
            }
            weather?.value?.let {
                this.weather = it
            }
        }
    }

// -------------------------------------------------------------------------------------------------
// ----------------------------------------- DataBinding -------------------------------------------
// -------------------------------------------------------------------------------------------------
}

class Indexed(var index: Int, var item: WeatherDTO)




