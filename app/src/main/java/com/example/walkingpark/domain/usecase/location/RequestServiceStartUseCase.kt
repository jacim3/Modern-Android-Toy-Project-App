package com.example.walkingpark.domain.usecase.location

import com.example.walkingpark.domain.LocationServiceRepository
import javax.inject.Inject

class RequestServiceStartUseCase @Inject constructor(
    private val locationServiceRepository: LocationServiceRepository
) {

    operator fun invoke(){

    }
}