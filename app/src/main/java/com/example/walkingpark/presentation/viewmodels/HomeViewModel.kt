package com.example.walkingpark.presentation.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.example.walkingpark.constants.Settings
import com.example.walkingpark.data.model.dto.AirResponse
import com.example.walkingpark.data.model.dto.StationResponse
import com.example.walkingpark.data.model.dto.WeatherResponse
import com.example.walkingpark.data.model.entity.LocationEntity
import com.example.walkingpark.data.model.ResponseCheck
import com.example.walkingpark.data.model.entity.paging.Weathers
import com.example.walkingpark.data.repository.AirApiRepository
import com.example.walkingpark.data.repository.GeocodingRepository
import com.example.walkingpark.data.repository.StationApiRepository
import com.example.walkingpark.data.repository.WeatherApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import javax.inject.Inject
import kotlin.math.abs

/*
    TODO 현재는 UI 관련 비즈니스 로직을 작성하지 않았으므로 사용하지 않음.
*/

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
    val userLiveHolderWeather = MutableLiveData<List<WeatherResponse.Response.Body.Items.Item>>()

    var isAirLoaded = MutableLiveData<Boolean>()
    var isStationLoaded = MutableLiveData<Boolean>()
    var isWeatherLoaded = MutableLiveData<Boolean>()

    val userResponseCheck = MediatorLiveData<ResponseCheck>().apply {
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

    fun startGeocodingBeforeStationApi(entity: LocationEntity) {

        if (isStationLoaded.value == null || isStationLoaded.value == true) {
            return
        }

        geocodingRepository.getAddressSet(entity)
            .retry(5)
            .subscribe(
                {
                    startStationApi(entity, it)
                },
                {
                    it.printStackTrace()
                }
            )
    }

    @SuppressLint("CheckResult")
    private fun startStationApi(entity: LocationEntity, addresses: List<String>) {

        stationRepository.startStationApi(addresses)
            .retryWhen { error ->
                error.zipWith(
                    Flowable.range(1, 3)
                ) { _, t2 -> t2 }.flatMap {
                    Flowable.timer(it.toLong(), TimeUnit.SECONDS)
                }
            }
            .subscribeBy(
                onSuccess = { response ->
                    userLiveHolderStation.postValue(
                        getNearestLocation(response.response.body.items, entity)
                    )
                    isStationLoaded.postValue(true)
                },
                onError = {
                    it.printStackTrace()
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

    fun startAirApi(stationName: String) {
        viewModelScope.launch {
            airRepository.startAirApi(stationName)
                .retryWhen { error ->
                    error.zipWith(
                        Flowable.range(1, 3)
                    ) { _, t2 -> t2 }.flatMap {
                        Flowable.timer(it.toLong(), TimeUnit.SECONDS)
                    }
                }
                .subscribeBy(
                    onSuccess = { response ->
                        userLiveHolderAir.postValue(response.response.body.items)
                        isAirLoaded.postValue(true)
                        Log.e("asdfadsfafds", response.toString())
                    },
                    onError = {
                        it.printStackTrace()
                    }
                )

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun startWeatherApi(entity: LocationEntity): Flowable<PagingData<Weathers.Weather>> {

        // val compositeDisposable = CompositeDisposable()

        return weatherRepository
            .startWeatherApi(entity)
            .observeOn(AndroidSchedulers.mainThread())
            .cachedIn(viewModelScope)


/*            weatherRepository.startApi(entity)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen { error ->
                    error.zipWith(
                        Flowable.range(1, 3)
                    ) { _, t2 -> t2 }.flatMap {
                        Flowable.timer(it.toLong(), TimeUnit.SECONDS)
                    }
                }
                .subscribeBy(
                    onSuccess = { response ->
                        val concertList:
                                Single<PagedList< List<WeatherDTO.Response.Body.Items.Item>>>

                        userLiveHolderWeather.value = response.response.body.items.item
                        isWeatherLoaded.postValue(true)
                    },
                    onError = {

                    },
                )*/


/*            response?.let {
                userLiveHolderWeather.value = it
                val tmpMap = userLiveHolderLoadedStatus.value
                tmpMap!!["weather"] = "success"
                userLiveHolderLoadedStatus.postValue(tmpMap)
            }*/


    }

    private fun combineResponses(
        station: MutableLiveData<Boolean>?,
        air: MutableLiveData<Boolean>?,
        weather: MutableLiveData<Boolean>?
    ): ResponseCheck {
        return ResponseCheck(air = false, station = false, weather = false).apply {
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
}

// -------------------------------------------------------------------------------------------------
// ----------------------------------------- DataBinding -------------------------------------------
// -------------------------------------------------------------------------------------------------
