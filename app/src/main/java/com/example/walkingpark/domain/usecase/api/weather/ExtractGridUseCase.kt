package com.example.walkingpark.domain.usecase.api.weather

import com.example.walkingpark.domain.WeatherApiRepository
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class ExtractGridUseCase @Inject constructor(
    private val weatherRepository: WeatherApiRepository
) {

    operator fun invoke(latLng: LatLng) = weatherRepository.changeLatLngToGrid(latLng)
}