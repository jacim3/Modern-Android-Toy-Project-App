package com.example.walkingpark.domain.usecase.api.weather.parent

import com.example.walkingpark.domain.usecase.api.weather.child.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 *   Parent UseCase 는 child 패키지의 항목들을 통합. 호출순서대로 패러미터를 넘겨받아 순차적으로 처리되며
 *   1. getGridUseCase - 사용자의 현재 위치를 기준, 이를 지도 격자값으로 리턴. (LatLngToGridXy.class)
 *   2. getTImeUseCase - 사용자의 현재 시간을 기준, 검색을 위하여 가장 적합한 시간을 처리하여 리턴.
 *   3. getQueryUseCase - 위에서 리턴한 데이터들을 패러미터로 받아 api 통신에 필요한 쿼리 생성 후 리턴
 *   4. getWeatherUseCase - 처리된 쿼리를 전달받아 Api 통신 후 response 리턴
 *   5. TODO response 를 처리하여 리턴할 비즈니스 로직을 및 이를 매핑할 UseCase
 **/
class ResultWeatherUseCase @Inject constructor(
    private val getGridUseCase: GetGridUseCase,
    private val getTimeUseCase: GetTimeUseCase,
    private val getQueryUseCase: GetQueryUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val handleResponseUseCase: HandleResponseUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(latLng: LatLng) = withContext(defaultDispatcher) {

        handleResponseUseCase(
            getWeatherUseCase(
                getQueryUseCase(
                    getTimeUseCase(),
                    getGridUseCase(latLng)
                )
            )
        )
    }
}