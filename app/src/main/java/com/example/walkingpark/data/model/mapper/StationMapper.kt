package com.example.walkingpark.data.model.mapper

import com.example.walkingpark.data.model.dto.WeatherResponse
import com.example.walkingpark.data.model.entity.paging.Weathers

class StationMapper {

    fun transform(response: WeatherResponse.Response.Body) : Weathers {
        return with(response) {
            Weathers(
                total = totalCount,
                page = pageNo,
                weathers = response.items.item.map {
                    Weathers.Weather(
                        id = 0,
                        baseDate = it.baseDate,
                        baseTime = it.baseTime,
                        category = it.category,
                        showDate = it.fcstDate,
                        showTime = it.fcstTime,
                        nx = it.nx,
                        ny = it.ny
                    )
                }
            )
        }
    }
}