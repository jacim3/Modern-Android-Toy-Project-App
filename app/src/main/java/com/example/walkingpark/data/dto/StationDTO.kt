package com.example.walkingpark.data.dto

import com.google.gson.annotations.SerializedName


data class StationDTO(
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

                @SerializedName("dmX") val dmX: Double,
                @SerializedName("item") val item: String,
                @SerializedName("mangName") val mangName: String,
                @SerializedName("year") val year: Int,
                @SerializedName("addr") val addr: String,
                @SerializedName("stationName") val stationName: String,
                @SerializedName("dmY") val dmY: Double
            )
        }

        data class Header(

            @SerializedName("resultMsg") val resultMsg: String,
            @SerializedName("resultCode") val resultCode: Int
        )
    }
}





