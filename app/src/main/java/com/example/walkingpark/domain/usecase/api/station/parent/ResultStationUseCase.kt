package com.example.walkingpark.domain.usecase.api.station.parent

import android.location.Address
import com.example.walkingpark.domain.usecase.api.station.child.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
*   Parent UseCase 는 child 패키지의 항목들을 통합. 호출순서대로 패러미터를 넘겨받아 순차적으로 처리되며
*   1. getQueryUseCase - rest Api 통신을 하기 전, 이를 위한 api 쿼리를 처리하여 리턴
*   2. getStationUseCase - retrofit 모듈을 통한 api 통신 후 response 를 받아옴.
*   3. handleResponseUseCase - Api 로 부터 받은 데이터의 validation 체크 후 필요한 부분을 리턴
*   4. getNearStationUseCase - 받아온 response 에서, 사용자 위치 기준 가장 가짜운 미세먼지 측정소를 추출하여 리턴
*   5. TODO MAPPER 를 통한 데이터 파싱 후 ViewModel 에 전달.
**/

class ResultStationUseCase @Inject constructor(
    private val getQueryUseCase: GetQueryUseCase,
    private val getStationUseCase: GetStationUseCase,
    private val handleResponseUseCase: HandleResponseUseCase,
    private val getNearStationUseCase: GetNearStationUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(addresses: List<Address>, latLng: LatLng) =
        withContext(defaultDispatcher) {

            /*getNearStationUseCase(
                handleResponseUseCase(getStationUseCase(getQueryUseCase(addresses), latLng)), latLng
            )*/
            getNearStationUseCase(handleResponseUseCase(getStationUseCase(getQueryUseCase(addresses), latLng))!!, latLng)
        }
}