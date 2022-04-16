package com.example.walkingpark.domain.usecase.api.weather.parent

import com.example.walkingpark.domain.usecase.api.weather.child.GetGridUseCase
import com.example.walkingpark.domain.usecase.api.weather.child.GetQueryUseCase
import com.example.walkingpark.domain.usecase.api.weather.child.GetTimeUseCase
import com.example.walkingpark.domain.usecase.api.weather.child.GetWeatherUseCase
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ResultWeatherUseCase @Inject constructor(
    private val getGridUseCase: GetGridUseCase,
    private val getTimeUseCase: GetTimeUseCase,
    private val getQueryUseCase: GetQueryUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(latLng: LatLng) = withContext(defaultDispatcher) {

        getWeatherUseCase(
            getQueryUseCase(
                getTimeUseCase(),
                getGridUseCase(latLng)
            )
        )
    }
}