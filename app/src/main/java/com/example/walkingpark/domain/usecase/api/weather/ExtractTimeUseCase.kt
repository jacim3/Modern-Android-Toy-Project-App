package com.example.walkingpark.domain.usecase.api.weather

import com.example.walkingpark.domain.WeatherApiRepository
import javax.inject.Inject

class ExtractTimeUseCase @Inject constructor(
    private val weatherRepository: WeatherApiRepository
) {
    operator fun invoke() = weatherRepository.extractTime()
}