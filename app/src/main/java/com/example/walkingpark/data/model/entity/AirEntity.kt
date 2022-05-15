package com.example.walkingpark.data.model.entity

import com.google.gson.annotations.SerializedName

data class AirEntity(
    val so2Grade: String,
    val coFlag: String,
    val khaiValue: String,
    val so2Value: String,
    val coValue: String,
    val pm25Flag: String,
    val pm10Flag: String,
    val pm10Value: String,
    val o3Grade: String,
    val khaiGrade: String,
    val pm25Value: String,
    val no2Flag: String,
    val no2Grade: String,
    val o3Flag: String,
    val pm25Grade: String,
    val so2Flag: String,
    val dataTime: String,
    val coGrade: String,
    val no2Value: String,
    val pm10Grade: String,
    val o3Value: String
)
