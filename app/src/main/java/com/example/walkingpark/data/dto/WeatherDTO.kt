package com.example.walkingpark.data.dto

import com.google.gson.annotations.SerializedName
import retrofit2.Response

data class WeatherDTO(
    @SerializedName("response") val response: Response
) {
    data class Response(

        @SerializedName("header") val header: Header,
        @SerializedName("body") val body: Body
    ) {
        data class Header(

            @SerializedName("resultCode") val resultCode: Int,
            @SerializedName("resultMsg") val resultMsg: String
        )

        data class Body(

            @SerializedName("dataType") val dataType: String,
            @SerializedName("items") val items: Items,
            @SerializedName("pageNo") val pageNo: Int,
            @SerializedName("numOfRows") val numOfRows: Int,
            @SerializedName("totalCount") val totalCount: Int
        ) {
            data class Items(

                @SerializedName("item") val item: List<Item>
            ) {
                data class Item(

                    @SerializedName("baseDate") val baseDate: Int,
                    @SerializedName("baseTime") val baseTime: Int,
                    @SerializedName("category") val category: String,
                    @SerializedName("fcstDate") val fcstDate: Int,
                    @SerializedName("fcstTime") val fcstTime: Int,
                    @SerializedName("fcstValue") val fcstValue: Int,
                    @SerializedName("nx") val nx: Int,
                    @SerializedName("ny") val ny: Int
                )
            }
        }
    }
}

