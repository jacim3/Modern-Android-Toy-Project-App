package com.example.walkingpark.domain.usecase.api.air.parent

import com.example.walkingpark.domain.usecase.api.air.child.GetAirUseCase
import com.example.walkingpark.domain.usecase.api.air.child.GetQueryUseCase
import com.example.walkingpark.domain.usecase.api.air.child.HandleResponseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
*   Parent UseCase 는 child 패키지의 항목들을 통합. 호출순서대로 패러미터를 넘겨받아 처리함.
*   1. getQueryUseCase - rest Api 통신을 하기 전, 이를 위한 api 쿼리를 처리하여 리턴
*   2. getAirUseCase - rest Api 를 통하여 response 를 받아옴.
*   3. handleResponseUseCase - Api 로 부터 받은 데이터의 validation 체크 후 필요한 부분을 리턴
*   4. TODO MAPPER 를 통한 데이터 파싱 후 ViewModel 에 전달.
**/

class ResultAirUseCase @Inject constructor(
    private val getQueryUseCase: GetQueryUseCase,
    private val getAirUseCase: GetAirUseCase,
    private val handleResponseUseCase: HandleResponseUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend operator fun invoke(stationName: String) = withContext(defaultDispatcher) {
        handleResponseUseCase(getAirUseCase(getQueryUseCase(stationName)))
    }
}