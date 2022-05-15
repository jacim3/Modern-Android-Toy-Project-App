package com.example.walkingpark.data.repository

import com.example.walkingpark.data.model.entity.LocationEntity
import com.example.walkingpark.data.source.GeocodingDataSource
import io.reactivex.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeocodingRepository @Inject constructor(
    private val geocodingDataSource: GeocodingDataSource
) {
    fun getAddressSet(entity: LocationEntity): Single<List<String>> = geocodingDataSource.getAddressSet(entity)
}