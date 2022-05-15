package com.example.walkingpark.data.model.entity

data class LocationSearchEntity(
    val startLatitude:Double,
    val endLatitude:Double,
    val startLongitude:Double,
    val endLongitude:Double,
    val adjustValue:Double
)