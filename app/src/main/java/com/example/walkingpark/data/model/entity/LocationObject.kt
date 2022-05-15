package com.example.walkingpark.data.model.entity

// Gps 위치정보 발행에 timestamp 값을 같이 저장.
data class LocationObject(
    val latitude: Double,
    val longitude: Double,
    val time: Long
)