package com.example.walkingpark.data.source

import com.example.walkingpark.data.model.entity.LocationSearchEntity
import com.example.walkingpark.data.room.AppDatabase
import com.example.walkingpark.data.room.ParkDB
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RoomDataSource @Inject constructor(
    private val appDatabase: AppDatabase
) {
    // DB 의 (A Between B) And (C between D) 쿼리를 수행하여 결과를 받아옴.
    fun searchDatabase(query: LocationSearchEntity): Single<List<ParkDB>> {

        return appDatabase.parkDao().queryRangedDataFromLatLng(
            startLat = query.startLatitude,
            endLat = query.endLatitude,
            startLng = query.startLongitude,
            endLng = query.endLongitude,
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}