package com.example.walkingpark.data.source

import android.location.Geocoder
import com.example.walkingpark.constants.Settings
import com.example.walkingpark.data.model.AddressToString
import com.example.walkingpark.data.model.entity.LocationEntity
import io.reactivex.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeocodingDataSource @Inject constructor(
    private val geocoder: Geocoder
) {
    fun getAddressSet(entity: LocationEntity): Single<List<String>> =
        Single.just(AddressToString(
            geocoder.getFromLocation(
                entity.latitude,
                entity.longitude,
                Settings.LOCATION_ADDRESS_SEARCH_COUNT
            )
        ))
}