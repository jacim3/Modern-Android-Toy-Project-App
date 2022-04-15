package com.example.walkingpark.domain.usecase.api.air

import com.example.walkingpark.domain.AirApiRepository
import javax.inject.Inject

class ExtractQueryUseCase @Inject constructor(
    private val airRepository: AirApiRepository
){

    operator fun invoke(stationName: String) =  airRepository.extractQuery(stationName)
}