package com.example.walkingpark.domain.usecase.api.weather.child

import com.example.walkingpark.domain.WeatherApiRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherApiRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend operator fun invoke(query: Map<String, String>) = withContext(defaultDispatcher) {

        weatherRepository.startWeatherApi(query)
    }
}