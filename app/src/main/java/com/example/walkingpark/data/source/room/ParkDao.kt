package com.example.walkingpark.data.source.room

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ParkDao {

    //      @Query("SELECT * FROM ParkDB WHERE (field6 BETWEEN :startLat AND :endLat) And (field7 BETWEEN :startLng AND :endLng) ")
    @Query("SELECT * FROM ParkDB WHERE (field6 BETWEEN :startLat AND :endLat) And (field7 BETWEEN :startLng AND :endLng) ")
    suspend fun queryRangedDataFromLatLng(startLat:Double, endLat:Double, startLng:Double, endLng:Double):List<ParkDB>

    @Query("SELECT * FROM ParkDB")
    suspend fun getAll() : List<ParkDB>
}