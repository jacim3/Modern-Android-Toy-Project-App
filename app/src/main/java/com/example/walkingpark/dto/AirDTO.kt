package com.example.walkingpark.dto

import com.google.gson.annotations.SerializedName
import retrofit2.Response


data class AirDTO(

    @SerializedName("response") val response: Response
) {
    data class Response(

        @SerializedName("body") val body: Body,
        @SerializedName("header") val header: Header
    ) {
        data class Body(

            @SerializedName("totalCount") val totalCount: Int,
            @SerializedName("items") val items: List<Items>,
            @SerializedName("pageNo") val pageNo: Int,
            @SerializedName("numOfRows") val numOfRows: Int
        ) {
            data class Items(

                @SerializedName("so2Grade") val so2Grade: Int,
                @SerializedName("coFlag") val coFlag: String,
                @SerializedName("khaiValue") val khaiValue: Int,
                @SerializedName("so2Value") val so2Value: Double,
                @SerializedName("coValue") val coValue: Double,
                @SerializedName("pm25Flag") val pm25Flag: String,
                @SerializedName("pm10Flag") val pm10Flag: String,
                @SerializedName("pm10Value") val pm10Value: Int,
                @SerializedName("o3Grade") val o3Grade: Int,
                @SerializedName("khaiGrade") val khaiGrade: Int,
                @SerializedName("pm25Value") val pm25Value: Int,
                @SerializedName("no2Flag") val no2Flag: String,
                @SerializedName("no2Grade") val no2Grade: Int,
                @SerializedName("o3Flag") val o3Flag: String,
                @SerializedName("pm25Grade") val pm25Grade: Int,
                @SerializedName("so2Flag") val so2Flag: String,
                @SerializedName("dataTime") val dataTime: String,
                @SerializedName("coGrade") val coGrade: Int,
                @SerializedName("no2Value") val no2Value: Double,
                @SerializedName("pm10Grade") val pm10Grade: Int,
                @SerializedName("o3Value") val o3Value: Double
            )
        }

        data class Header(
            @SerializedName("resultMsg") val resultMsg: String,
            @SerializedName("resultCode") val resultCode: Int
        )
    }
}