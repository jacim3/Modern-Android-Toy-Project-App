package com.example.walkingpark.data.source

import android.annotation.SuppressLint
import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.example.walkingpark.constants.Common
import com.example.walkingpark.data.api.PublicApiService
import com.example.walkingpark.data.model.dto.WeatherDTO
import com.example.walkingpark.data.model.entity.LocationEntity
import com.example.walkingpark.data.tools.LatLngToGridXy
import com.example.walkingpark.di.module.PublicDataApiModule
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PagingSource @Inject constructor(
    private val apiKey: String,
    @PublicDataApiModule.WeatherApi
    private val weatherApi: PublicApiService
) : RxPagingSource<String, WeatherDTO>() {

    lateinit var userLocation: LocationEntity

    override fun loadSingle(params: LoadParams<String>): Single<LoadResult<String, WeatherDTO>> {
        val page = params.key
        return weatherApi.getWeatherByGridXY(serviceKey = apiKey, getQuery(userLocation))
            .subscribeOn(Schedulers.io())

    }

    override fun getRefreshKey(state: PagingState<String, WeatherDTO>): String? {
        TODO("Not yet implemented")
    }

    fun startApi(entity: LocationEntity) = apiDataSource.getWeatherApi(getQuery(entity))


    private fun getQuery(entity: LocationEntity) =
        dataToQuery(getTime(getCalendar()), LatLngToGridXy(entity.latitude, entity.longitude))


    private fun dataToQuery(
        timeMap: Map<String, String>,
        grid: LatLngToGridXy
    ): Map<String, String> {
        return mapOf(
            Pair("dataType", "json"),
            Pair("base_date", timeMap["date"].toString()),
            Pair("base_time", timeMap["time"].toString()),
            Pair("numOfRows", "1000"),
            Pair("nx", grid.locX.toString()),
            Pair("ny", grid.locY.toString())
        )
    }


    // Weather Api 는 1시간 단위로, 현재 시각에서 1시간 전 까지의 데이터를 요청 가능. 또한 AM 02:00 부터 데이터 요청 가능
    @SuppressLint("SimpleDateFormat")
    private fun getTime(cal: Calendar): Map<String, String> {
        return mapOf(
            Pair("date", Common.dateFormat.format(cal.time)),
            Pair("time", Common.timeFormat.format(cal.time))
        )
    }


    private fun getCalendar(): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.MINUTE, 0)
            set(Calendar.HOUR, -1)
            // 1일전 23시로 돌리기
            if (this.get(Calendar.HOUR) < 2) {
                set(Calendar.HOUR, 23)
                add(Calendar.DATE, -1)
            }
        }
    }

    fun getUserLocation(entity: LocationEntity){
        userLocation = entity
    }

}