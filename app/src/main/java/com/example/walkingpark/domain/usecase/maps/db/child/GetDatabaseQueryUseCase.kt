package com.example.walkingpark.domain.usecase.maps.db.child

import com.example.walkingpark.domain.repository.MapsRepository
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class GetDatabaseQueryUseCase @Inject constructor(
    private val mapsRepository : MapsRepository
) {

    // 위경도, 커서값, 가중치 (검색 실패 시, 범위 확대후 검색을 위함.)
    operator fun invoke(latLng: LatLng, cursorValue:Int, mult:Int) =
        mapsRepository.getDatabaseQuery(latLng, cursorValue, mult)
}