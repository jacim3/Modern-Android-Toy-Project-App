package com.example.walkingpark.ui.adapter.home

import com.example.walkingpark.R

const val NIGHT_START = 20
const val NIGHT_END = 5
const val AM = 0
const val PM = 1

const val ITEM = 0
const val SEPERATOR = 1

val WEATHER_ICONS = arrayOf(
    arrayOf(
        0,
        R.drawable.ic_weather_am_clear,
        0,
        R.drawable.ic_weather_am_cloudy,
        R.drawable.ic_weather_am_overcast
    ),
    arrayOf(
        0,
        R.drawable.ic_weather_pm_clear,
        0,
        R.drawable.ic_weather_pm_cloudy,
        R.drawable.ic_weather_pm_overcast
    )
)

val RAIN_ICONS = arrayOf(
    arrayOf(
        0,
        R.drawable.ic_weather_am_rain,
        R.drawable.ic_weather_snow_rain,
        R.drawable.ic_weather_snow,
        R.drawable.ic_weather_am_shower
    ),
    arrayOf(
        0,
        R.drawable.ic_weather_pm_rain,
        R.drawable.ic_weather_snow_rain,
        R.drawable.ic_weather_snow,
        R.drawable.ic_weather_pm_shower
    )
)