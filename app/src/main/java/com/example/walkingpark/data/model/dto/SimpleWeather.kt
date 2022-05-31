package com.example.walkingpark.data.model.dto

data class SimpleWeather (
    val windIcon:Int,
    val windValue:String,
    val humidityIcon:Int,
    val humidityValue:String,
    val rainChanceIcon:Int,
    val rainChanceValue:String,
    val date:String,
    val time:String
    )