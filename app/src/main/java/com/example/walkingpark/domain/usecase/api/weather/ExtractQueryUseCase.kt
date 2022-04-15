package com.example.walkingpark.domain.usecase.api.weather

import com.example.walkingpark.data.tools.LatLngToGridXy
import com.example.walkingpark.domain.WeatherApiRepository
import javax.inject.Inject

class ExtractQueryUseCase @Inject constructor(
    private val weatherRepository: WeatherApiRepository
) {
    operator fun invoke(timeMap:Map<String, String>, grid: LatLngToGridXy) = weatherRepository.extractQuery(timeMap, grid)
}