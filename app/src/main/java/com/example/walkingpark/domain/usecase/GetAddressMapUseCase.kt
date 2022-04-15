package com.example.walkingpark.domain.usecase

import android.location.Address
import com.example.walkingpark.domain.LocationServiceRepository
import java.util.HashMap
import javax.inject.Inject


class GetAddressMapUseCase @Inject constructor(
    private val locationServiceRepository: LocationServiceRepository
) {
    operator fun invoke(addresses: MutableList<Address>): HashMap<Char, String?>? {
        return locationServiceRepository.getAddressFromLocation(addresses)
    }
}