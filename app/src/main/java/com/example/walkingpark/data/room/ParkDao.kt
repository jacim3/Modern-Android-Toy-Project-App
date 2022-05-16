package com.example.walkingpark.data.room

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single

@Dao
interface ParkDao {

    //      @Query("SELECT * FROM ParkDB WHERE (field6 BETWEEN :startLat AND :endLat) And (field7 BETWEEN :startLng AND :endLng) ")
    @Query("SELECT * FROM ParkDB WHERE (field6 BETWEEN :startLat AND :endLat) And (field7 BETWEEN :startLng AND :endLng) ")
    fun queryRangedDataFromLatLng(startLat:Double, endLat:Double, startLng:Double, endLng:Double): Single<List<ParkDB>>

    @Query("SELECT * FROM ParkDB")
    fun getAll() : List<ParkDB>
}
