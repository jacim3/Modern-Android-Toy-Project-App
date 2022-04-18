package com.example.walkingpark.domain.usecase.api.weather.child

import com.example.walkingpark.domain.model.WeatherDTO
import com.example.walkingpark.domain.repository.WeatherApiRepository
import retrofit2.Response
import javax.inject.Inject

class HandleResponseUseCase @Inject constructor(
    private val weatherRepository: WeatherApiRepository
) {
    operator fun invoke(response: Response<WeatherDTO>) = weatherRepository.handleResponse(response)
}