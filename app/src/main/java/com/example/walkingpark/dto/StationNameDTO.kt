package com.example.walkingpark.dto

import com.google.gson.annotations.SerializedName



    data class StationNameDTO (
        @SerializedName("response") val response : Response
    ) {
        data class Response (

            @SerializedName("body") val body : Body,
            @SerializedName("header") val header : Header
        ) {
            data class Header (

                @SerializedName("resultMsg") val resultMsg : String,
                @SerializedName("resultCode") val resultCode : Int
            )

            data class Body (

                @SerializedName("totalCount") val totalCount : Int,
                @SerializedName("items") val items : List<Items>,
                @SerializedName("pageNo") val pageNo : Int,
                @SerializedName("numOfRows") val numOfRows : Int
            ) {
                data class Items (

                    @SerializedName("sggName") val sggName : String,
                    @SerializedName("umdName") val umdName : String,
                    @SerializedName("tmX") val tmX : Double,
                    @SerializedName("tmY") val tmY : Double,
                    @SerializedName("sidoName") val sidoName : String
                )
            }
        }
    }




