package com.example.walkingpark.domain

import android.location.Address
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.util.HashMap

interface LocationServiceRepository {

    fun getAddressFromLocation(locations: MutableList<Address>): HashMap<Char, String?>?

    fun getLocationCallback(result: Location) : LatLng

    fun startLocationService(){

    }

    fun startLocationUpdate(){

    }

    fun stopLocationService(){

    }
}