package com.example.walkingpark.data.model.dto

data class SimpleAir(
    val dustStatus: String,
    val smallDustStatus: String,
    val dust: String,
    val smallDust: String,
    val dateTime: String,
    val stationName: String,
    val icon: Int
)