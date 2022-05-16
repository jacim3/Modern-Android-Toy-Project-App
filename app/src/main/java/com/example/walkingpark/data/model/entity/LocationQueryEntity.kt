package com.example.walkingpark.data.model.entity

data class LocationQueryEntity(
    val lat1: Float,
    val lat2: Float,
    val lng1: Float,
    val lng2: Float,
    val adjustValue: Int
)