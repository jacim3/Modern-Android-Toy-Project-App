package com.example.walkingpark.domain.usecase.api.air.child

import com.example.walkingpark.domain.AirApiRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAirUseCase @Inject constructor(
    private val airRepository: AirApiRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend operator fun invoke(query: Map<String, String>) = withContext(defaultDispatcher) {
        airRepository.startAirApi(query)
    }
}