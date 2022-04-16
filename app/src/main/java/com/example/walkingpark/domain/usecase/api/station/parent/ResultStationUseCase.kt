package com.example.walkingpark.domain.usecase.api.station.parent

import android.location.Address
import com.example.walkingpark.domain.usecase.api.station.child.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ResultStationUseCase @Inject constructor(
    private val getQueryUseCase: GetQueryUseCase,
    private val getStationUseCase: GetStationUseCase,
    private val getNearStationUseCase: GetNearStationUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(addresses: List<Address>, latLng: LatLng) =
        withContext(defaultDispatcher) {

            getNearStationUseCase(
                getStationUseCase(
                    getQueryUseCase(addresses),
                    latLng
                ), latLng
            )
        }
}