package com.example.walkingpark.domain.usecase.api.weather.child

import com.example.walkingpark.domain.WeatherApiRepository
import javax.inject.Inject

class GetTimeUseCase @Inject constructor(
    private val weatherRepository: WeatherApiRepository
) {
    operator fun invoke() = weatherRepository.extractTime()
}