package com.example.walkingpark.domain.usecase.api.weather.child

import com.example.walkingpark.domain.repository.WeatherApiRepository
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class GetGridUseCase @Inject constructor(
    private val weatherRepository: WeatherApiRepository
) {

    operator fun invoke(latLng: LatLng) = weatherRepository.changeLatLngToGrid(latLng)
}