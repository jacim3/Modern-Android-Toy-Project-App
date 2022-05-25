package com.example.walkingpark.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.walkingpark.constants.Common
import com.example.walkingpark.data.model.dto.AirResponse
import com.example.walkingpark.data.model.dto.StationResponse
import com.example.walkingpark.data.model.dto.WeatherResponse
import com.example.walkingpark.data.model.entity.LocationEntity
import com.example.walkingpark.data.model.ResponseCheck
import com.example.walkingpark.data.repository.AirApiRepository
import com.example.walkingpark.data.repository.GeocodingRepository
import com.example.walkingpark.data.repository.StationApiRepository
import com.example.walkingpark.data.repository.WeatherApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
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
    val userLiveHolderWeather = MutableLiveData<Map<String, Map<String, Map<String, String>>>>()

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
                ) { _, t2 -> t2 }.flatMap {
                    Flowable.timer(it.toLong(), TimeUnit.SECONDS)
                }
            }

            .subscribeBy(
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

    // 측정소 결과 리스트 중 사용자와 가장 가까운 위치 결과 받아내기.
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
    fun startWeatherApi(entity: LocationEntity): Disposable {

/*        .retry { count, error ->
            Timestamp(Calendar.getInstance().apply {
                add(Calendar.HOUR, count * -1)
            }.timeInMillis).time
            count < 3
        }*/

        // 날씨 Api 의 결과는 총 800개가 넘으므로, 이를 250개씩 4페이지에 걸쳐 분할하여 받음.
        return Single.zip(
            weatherRepository.startWeatherApi(entity, 1),
            weatherRepository.startWeatherApi(entity, 2),
            weatherRepository.startWeatherApi(entity, 3),
            weatherRepository.startWeatherApi(entity, 4),
        ) { emit1, emit2, emit3, emit4 ->
            weatherResponseToMap(weatherResponseCheckAndMerge(listOf(emit1, emit2, emit3, emit4)))
        }.subscribeBy(
            onSuccess = {
                isWeatherLoaded.postValue(Common.RESPONSE_SUCCESS)
                userLiveHolderWeather.postValue(it)
                Log.e("WeatherResponse", "Success")
            },
            onError = {
                isWeatherLoaded.postValue(Common.RESPONSE_FAILURE)
                it.printStackTrace()
                Log.e("WeatherResponse", "Failure")
            }
        )
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

    // Chart 데이터로 제공하기 위한 Map 객체 출력
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
