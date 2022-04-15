package com.example.walkingpark.domain.usecase.location

import android.location.Address
import com.example.walkingpark.domain.LocationServiceRepository
import java.util.HashMap
import javax.inject.Inject

class GetAddressFromGeocoderUseCase @Inject constructor(
    private val locationServiceRepository: LocationServiceRepository
) {
    operator fun invoke(address: MutableList<Address>): HashMap<Char, String?>? {
        return locationServiceRepository.getAddressFromLocation(address)
    }
}