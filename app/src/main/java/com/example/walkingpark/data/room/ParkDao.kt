package com.example.walkingpark.data.room

import androidx.room.Dao
import androidx.room.Query
import com.google.android.gms.maps.model.LatLng

@Dao
interface ParkDao {

    //      @Query("SELECT * FROM ParkDB WHERE (field6 BETWEEN :startLat AND :endLat) And (field7 BETWEEN :startLng AND :endLng) ")
    @Query("SELECT * FROM ParkDB WHERE (field7 BETWEEN :startLng AND :endLng) ")
    suspend fun queryRangedDataFromLatLng(startLng:Double, endLng:Double):List<ParkDB>
}
