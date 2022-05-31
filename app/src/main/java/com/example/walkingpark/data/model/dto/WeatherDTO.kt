package com.example.walkingpark.data.model.dto

data class WeatherDTO(
    val date: String,
    val time: String,
    val temperature: String,
    val temperatureMax: String,
    val temperatureMin: String,
    val humidity: String,
    val rainChance: String,
    val rainType: String,
    val sky: String,
    val snow: String,
    val windSpeed: String,
    val windNS: String,
    val windEW: String
)