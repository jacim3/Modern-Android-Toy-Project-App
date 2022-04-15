package com.example.walkingpark.data.source.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
*   assets 의 db 파일과 제약조건을 동일학 맞춰줄 것!
*      
*
* */

@Entity
data class ParkDB(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pk") val pk:Int?,
    @ColumnInfo(name = "field1" ) val manageNumber: String?,
    @ColumnInfo(name = "field2" ) val parkName: String?,
    @ColumnInfo(name = "field3" ) val parkCategory: String?,
    @ColumnInfo(name = "field4" ) val addressDoro: String?,
    @ColumnInfo(name = "field5" ) val addressJibun: String?,
    @ColumnInfo(name = "field6" ) val latitude: Double?,
    @ColumnInfo(name = "field7" ) val longitude: Double?,
    @ColumnInfo(name = "field8" ) val parkSize: Double?,
    @ColumnInfo(name = "field9" ) val facilityHealth: String?,
    @ColumnInfo(name = "field10") val facilityJoy: String?,
    @ColumnInfo(name = "field11") val facilityUseFul: String?,
    @ColumnInfo(name = "field12") val facilityCulture: String?,
    @ColumnInfo(name = "field13") val facilityEtc: String?,
    @ColumnInfo(name = "field14") val dateDecision: String?,
    @ColumnInfo(name = "field15") val institutionName1: String?,
    @ColumnInfo(name = "field16") val phoneNumber: String?,
    @ColumnInfo(name = "field17") val dateReference: String?,
    @ColumnInfo(name = "field18") val institutionCode: String?,
    @ColumnInfo(name = "field19") val institutionName2: String?
)