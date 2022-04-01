package com.example.walkingpark.dto

import com.google.gson.annotations.SerializedName

// https://www.json2kotlin.com/results.php

data class ParkDTO (

    @SerializedName("response") val response : Response
){
    data class Response (

        @SerializedName("header") val header : Header,
        @SerializedName("body") val body : Body
    ) {
        data class Body (

            @SerializedName("items") val items : List<Items>,
            @SerializedName("totalCount") val totalCount : Int,
            @SerializedName("numOfRows") val numOfRows : Int,
            @SerializedName("pageNo") val pageNo : Int
        ) {
            data class Items (

                @SerializedName("manageNo") val manageNumber : String,
                @SerializedName("parkNm") val parkNumber : String,
                @SerializedName("parkSe") val parkCategory : String,
                @SerializedName("rdnmadr") val adrDoro : String,
                @SerializedName("lnmadr") val adrZibun : String,
                @SerializedName("latitude") val latitude : Double,
                @SerializedName("longitude") val longitude : Double,
                @SerializedName("parkAr") val parkSize : Int,
                @SerializedName("mvmFclty") val HealthFacility : String,
                @SerializedName("amsmtFclty") val joyFacility : String,
                @SerializedName("cnvnncFclty") val usefulFacility : String,
                @SerializedName("cltrFclty") val cultureFacility : String,
                @SerializedName("etcFclty") val etcFacility : String,
                @SerializedName("appnNtfcDate") val noticeDate : String,
                @SerializedName("institutionNm") val institutionNumber : String,
                @SerializedName("phoneNumber") val phoneNumber : String,
                @SerializedName("referenceDate") val referenceDate : String,
                @SerializedName("insttCode") val institutionCode : Int
            )
        }
        data class Header (

            @SerializedName("resultCode") val resultCode : Int,
            @SerializedName("resultMsg") val resultMsg : String,
            @SerializedName("type") val type : String
        )
    }
}







