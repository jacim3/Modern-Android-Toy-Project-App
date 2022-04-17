package com.example.walkingpark.domain.usecase.api.weather.child

import com.example.walkingpark.domain.model.tools.LatLngToGridXy
import com.example.walkingpark.domain.repository.WeatherApiRepository
import javax.inject.Inject

class GetQueryUseCase @Inject constructor(
    private val weatherRepository: WeatherApiRepository
) {
    operator fun invoke(timeMap:Map<String, String>, grid: LatLngToGridXy) = weatherRepository.getQuery(timeMap, grid)
}