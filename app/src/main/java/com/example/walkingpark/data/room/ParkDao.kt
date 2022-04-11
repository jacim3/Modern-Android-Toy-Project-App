package com.example.walkingpark.data.room

import androidx.room.Dao
import androidx.room.Query
import com.google.android.gms.maps.model.LatLng

@Dao
interface ParkDao {

    //  @Query("SELECT * FROM ParkDB WHERE (field6 BETWEEN :startLat AND :endLat ) AND (field6 BETWEEN :startLng AND :endLng )")
    @Query("SELECT * FROM ParkDB WHERE (field6 BETWEEN :startLat AND :endLat) And (field7 BETWEEN :startLng AND :endLng) ")
    suspend fun queryRangedDataFromLatLng(startLat:Double, endLat:Double, startLng:Double, endLng:Double):List<ParkDB>
}

@Dao
interface GridDao {
    @Query("SELECT * FROM GridDB WHERE f2=:address1")
    suspend fun getGridFromAddress1(address1:String): List<GridDB>

    @Query("SELECT * FROM GridDB WHERE f2=:address1 and f3=:address2")
    suspend fun getGridFromAddress2(address1:String, address2: String): List<GridDB>

    @Query("SELECT * FROM GridDB WHERE f2=:address1 and f3=:address2 and f4=:address3")
    suspend fun getGridFromAddress3(address1:String, address2: String, address3: String): List<GridDB>
}
