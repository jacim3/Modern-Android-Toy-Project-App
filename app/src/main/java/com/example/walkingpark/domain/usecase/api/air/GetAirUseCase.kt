package com.example.walkingpark.domain.usecase.api.air

import com.example.walkingpark.domain.AirApiRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAirUseCase @Inject constructor(
    private val airRepository: AirApiRepository,
    private val extractQueryUseCase: ExtractQueryUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend operator fun invoke(stationName: String) = withContext(defaultDispatcher) {
        airRepository.startAirApi(extractQueryUseCase(stationName) )
    }
}