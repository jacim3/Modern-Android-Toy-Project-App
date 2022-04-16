package com.example.walkingpark.domain.usecase.api.weather

import com.example.walkingpark.domain.StationApiRepository
import com.example.walkingpark.domain.WeatherApiRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherApiRepository,
    private val extractGridUseCase: ExtractGridUseCase,
    private val extractQueryUseCase: ExtractQueryUseCase,
    private val extractTimeUseCase: ExtractTimeUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend operator fun invoke(latLng: LatLng) = withContext(defaultDispatcher) {

        weatherRepository.startWeatherApi(
            extractQueryUseCase(
                extractTimeUseCase(),
                extractGridUseCase(latLng)
            )
        )
    }
}