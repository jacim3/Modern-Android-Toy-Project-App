package com.example.walkingpark.domain.usecase.api.air.parent

import com.example.walkingpark.domain.usecase.api.air.child.GetAirUseCase
import com.example.walkingpark.domain.usecase.api.air.child.GetQueryUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ResultAirUseCase @Inject constructor(
    private val getQueryUseCase: GetQueryUseCase,
    private val getAirUseCase: GetAirUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend operator fun invoke(stationName: String) = withContext(defaultDispatcher) {
        getAirUseCase(getQueryUseCase(stationName))
    }
}