package com.example.walkingpark.data.model.dto

import com.google.gson.annotations.SerializedName

/**
*   공공데이터 - 동네예보 API Retrofit2 통신결과의 직렬화를 위한 DTO 객체.
**/
data class WeatherResponse(
    @SerializedName("response") val response : Response
) {
    data class Response (

        @SerializedName("header") val header : Header,
        @SerializedName("body") val body : Body
    ) {
        data class Body (

            @SerializedName("dataType") val dataType : String,
            @SerializedName("items") val items : Items,
            @SerializedName("pageNo") val pageNo : Int,
            @SerializedName("numOfRows") val numOfRows : Int,
            @SerializedName("totalCount") val totalCount : Int
        ) {

            data class Items (

                @SerializedName("item") val item : List<Item>
            ) {
                data class Item (

                    @SerializedName("baseDate") val baseDate : String,
                    @SerializedName("baseTime") val baseTime : String,
                    @SerializedName("category") val category : String,
                    @SerializedName("fcstDate") val fcstDate : String,
                    @SerializedName("fcstTime") val fcstTime : String,
                    @SerializedName("fcstValue") val fcstValue : String,
                    @SerializedName("nx") val nx : String,
                    @SerializedName("ny") val ny : String
                )
            }
        }

        data class Header (

            @SerializedName("resultCode") val resultCode : Int,
            @SerializedName("resultMsg") val resultMsg : String
        )
    }
}

