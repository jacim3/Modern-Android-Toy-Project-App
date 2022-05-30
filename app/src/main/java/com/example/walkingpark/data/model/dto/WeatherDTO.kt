package com.example.walkingpark.data.model.dto

import com.example.walkingpark.presentation.adapter.home.TabAdapterHumidity

data class WeatherDTO(
    val date: String?,
    val time: String?,
    val temperature: String?,
    val temperatureMax: String?,
    val temperatureMin: String?,
    val humidity: String?,
    val rainChance: String?,
    val rainType: String,
    val snow: String?,
    val windSpeed: String?,
    val windNS: String?,
    val windEW: String?
)