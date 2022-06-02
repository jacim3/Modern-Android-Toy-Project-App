package com.example.walkingpark.data.model.dto.simple_panel

// 관심사 분리.
data class SimplePanel1 (
    val temperature: Map<String, String>,
    val weatherIcon:Int,
    val weatherText:String
)