package com.example.walkingpark.domain.usecase.api.station

import android.location.Address
import com.example.walkingpark.domain.StationApiRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetStationUseCase @Inject constructor(
    private val restApiRepository: StationApiRepository,
    private val extractQueryUseCase: ExtractQueryUseCase,
    private val extractNearStationUseCase: ExtractNearStationUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(addresses: List<Address>, latLng: LatLng) =
        withContext(defaultDispatcher) {

            extractNearStationUseCase(
                restApiRepository.startStationApi(
                    extractQueryUseCase(addresses),
                    latLng
                ), latLng
            )
        }
}