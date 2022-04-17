package com.example.walkingpark.domain.usecase.api.air.child

import com.example.walkingpark.domain.repository.AirApiRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
*   child 패키지의 UseCase 클래스는 Repository 를 참조하여, 어떠한 일을 처리하는데 수행되는 작업들을 세세하게 나누는 단위
*   이 패키지의 클래스는 일반적으로 혼자서 호출되지 않음.
**/

class GetAirUseCase @Inject constructor(
    private val airRepository: AirApiRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend operator fun invoke(query: Map<String, String>) = withContext(defaultDispatcher) {
        airRepository.startAirApi(query)
    }
}