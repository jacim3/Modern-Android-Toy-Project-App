package com.example.walkingpark.domain.usecase.api.air.child

import com.example.walkingpark.domain.AirApiRepository
import javax.inject.Inject

class GetQueryUseCase @Inject constructor(
    private val airRepository: AirApiRepository
){

    operator fun invoke(stationName: String) =  airRepository.extractQuery(stationName)
}