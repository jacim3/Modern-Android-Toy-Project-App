package com.example.walkingpark.data.model.entity

import com.google.gson.annotations.SerializedName

data class WeatherEntity(
    val baseDate: String,
    val baseTime: String,
    val category: String,
    val fcstDate: String,
    val fcstTime: String,
    val fcstValue: String,
    val nx: String,
    val ny: String
)