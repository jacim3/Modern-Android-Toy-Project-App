package com.example.walkingpark.domain.usecase.api.station.child

import android.location.Address
import com.example.walkingpark.domain.StationApiRepository
import javax.inject.Inject

class GetQueryUseCase @Inject constructor(
    private val stationRepository: StationApiRepository
){

    operator fun invoke(addresses: List<Address>) = stationRepository.extractQuery(addresses)

}