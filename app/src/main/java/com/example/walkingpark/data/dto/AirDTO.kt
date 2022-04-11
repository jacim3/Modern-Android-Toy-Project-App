package com.example.walkingpark.data.dto

import com.google.gson.annotations.SerializedName

/**
*   공공데이터 - 미세먼지 API Retrofit2 통신을 위한 DTO 객체.
**/

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
                //TODO 받아오는 데이터가 누락되는 경우도 있으므로, 자료형을 String 으로 선언할것!!
                @SerializedName("so2Grade") val so2Grade: String,
                @SerializedName("coFlag") val coFlag: String,
                @SerializedName("khaiValue") val khaiValue: String,
                @SerializedName("so2Value") val so2Value: String,
                @SerializedName("coValue") val coValue: String,
                @SerializedName("pm25Flag") val pm25Flag: String,
                @SerializedName("pm10Flag") val pm10Flag: String,
                @SerializedName("pm10Value") val pm10Value: String,
                @SerializedName("o3Grade") val o3Grade: String,
                @SerializedName("khaiGrade") val khaiGrade: String,
                @SerializedName("pm25Value") val pm25Value: String,
                @SerializedName("no2Flag") val no2Flag: String,
                @SerializedName("no2Grade") val no2Grade: String,
                @SerializedName("o3Flag") val o3Flag: String,
                @SerializedName("pm25Grade") val pm25Grade: String,
                @SerializedName("so2Flag") val so2Flag: String,
                @SerializedName("dataTime") val dataTime: String,
                @SerializedName("coGrade") val coGrade: String,
                @SerializedName("no2Value") val no2Value: String,
                @SerializedName("pm10Grade") val pm10Grade: String,
                @SerializedName("o3Value") val o3Value: String
            )
        }

        data class Header(
            @SerializedName("resultMsg") val resultMsg: String,
            @SerializedName("resultCode") val resultCode: Int
        )
    }
}