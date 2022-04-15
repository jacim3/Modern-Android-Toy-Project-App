package com.example.walkingpark.domain.usecase.api.station

import android.location.Address
import com.example.walkingpark.domain.StationApiRepository
import javax.inject.Inject

class ExtractQueryUseCase @Inject constructor(
    private val restApiRepository: StationApiRepository
){

    operator fun invoke(addresses: List<Address>) = restApiRepository.extractQuery(addresses)

}