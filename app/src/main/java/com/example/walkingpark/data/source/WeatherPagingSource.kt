package com.example.walkingpark.data.source

import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.example.walkingpark.data.api.PublicApiService
import com.example.walkingpark.data.model.mapper.WeatherMapper
import com.example.walkingpark.data.model.entity.paging.Weathers
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

private const val STARTING_PAGE_INDEX = 1

class WeatherPagingSource(
    private val apiKey:String,
    private val service: PublicApiService,
    private val query: Map<String, String>,
    private val mapper: WeatherMapper
) : RxPagingSource<Int, Weathers.Weather>() {


    private fun toLoadResult(data: Weathers, position: Int): LoadResult<Int, Weathers.Weather> {
        return LoadResult.Page(
            data = data.weathers,
            prevKey = if (position == 1) null else position - 1,
            nextKey = if (position == data.total) null else position + 1
        )
    }


    override fun getRefreshKey(state: PagingState<Int, Weathers.Weather>): Int? {
        return 0
    }


    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Weathers.Weather>> {
        val position = params.key ?: STARTING_PAGE_INDEX

        return service.getWeatherByGridXY(apiKey, query)
            .subscribeOn(Schedulers.io())
            .map {
                mapper.transform(it.response.body) }
            .map { toLoadResult(it, position) }
            .onErrorReturn { LoadResult.Error(it) }
    }
}
