package com.example.walkingpark.presentation.viewmodels

import android.app.Application
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.example.walkingpark.constants.Settings
import com.example.walkingpark.data.model.dto.AirDTO
import com.example.walkingpark.data.model.dto.StationDTO
import com.example.walkingpark.data.model.dto.WeatherDTO
import com.example.walkingpark.data.model.entity.LocationEntity
import com.example.walkingpark.data.model.ResponseCheck
import com.example.walkingpark.data.repository.AirApiRepository
import com.example.walkingpark.data.repository.StationApiRepository
import com.example.walkingpark.data.repository.WeatherApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
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
) : AndroidViewModel(application) {

    val userLiveHolderStation = MutableLiveData<StationDTO.Response.Body.Items?>()
    val userLiveHolderAir = MutableLiveData<List<AirDTO.Response.Body.Items>?>()
    val userLiveHolderWeather = MutableLiveData<List<WeatherDTO.Response.Body.Items.Item>>()

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

    fun startStationApi(entity: LocationEntity) {
        viewModelScope.launch {
            stationRepository.startStationApi(getGeocoding(entity))
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

/*            response?.let {
                userLiveHolderStation.postValue(it)
                startAirApi(it.stationName)
                val tmpMap = userLiveHolderLoadedStatus.value
                tmpMap!!["station"] = "success"
                userLiveHolderLoadedStatus.postValue(tmpMap)
            }*/
        }
    }

    // 측정소 결과 리스트 중 사용자와 가장 가까운 위치 결과 받아내기.
    private fun getNearestLocation(
        items: List<StationDTO.Response.Body.Items>,
        entity: LocationEntity
    ): StationDTO.Response.Body.Items? {

        return items.stream().sorted { p0, p1 ->
            (abs(p0.dmX - entity.latitude) + abs(p0.dmY - entity.longitude))
                .compareTo(
                    (abs(p1.dmX - entity.latitude) + abs(p1.dmY - entity.longitude))
                )
        }.collect(Collectors.toList())[0]
    }

    private fun getGeocoding(entity: LocationEntity): List<Address> {
        val coder = Geocoder(getApplication(), Locale.getDefault())
        return coder.getFromLocation(
            entity.latitude,
            entity.longitude,
            Settings.LOCATION_ADDRESS_SEARCH_COUNT
        )
    }

    fun startAirApi(stationName: String) {
        viewModelScope.launch {
            airRepository.getAirApi(stationName)
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
                    },
                    onError = {
                        it.printStackTrace()
                    }
                )

/*            response.let {
                userLiveHolderAir.postValue(it)
                val tmpMap = userLiveHolderLoadedStatus.value
                tmpMap!!["air"] = "success"
                userLiveHolderLoadedStatus.postValue(tmpMap)
            }*/
        }
    }

    fun startWeatherApi(entity: LocationEntity) {

        val compositeDisposable = CompositeDisposable()

        val observable: Flowable<PagedList> = weatherRepository.startApi(entity)



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

/*    private fun combineResponses(
        station: MutableLiveData<StationDTO.Response.Body.Items?>?,
        air: MutableLiveData<List<AirDTO.Response.Body.Items>?>?,
        weather: MutableLiveData<List<WeatherDTO.Response.Body.Items.Item>>?
    ): ResponseSet {
        return ResponseSet(null, null, null).apply {
            station?.let {
                this.station = it
            }
            air?.let {
                this.air = it
            }
            weather?.let {
                this.weather = it
            }
        }
    }*/

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
