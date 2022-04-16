package com.example.walkingpark.domain.usecase.api.station.child

import com.example.walkingpark.domain.StationApiRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetStationUseCase @Inject constructor(
    private val stationRepository: StationApiRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(
        query: Map<String, String>,
        latLng: LatLng
    ) =
        withContext(defaultDispatcher) {
            stationRepository.startStationApi(query, latLng)
        }
}