package com.example.walkingpark.dto

import android.util.Log
import com.google.gson.annotations.SerializedName

data class ParkDAO(
    @SerializedName("fields") val fields : List<Fields>,
    @SerializedName("records") val records : List<Records>
) {

    data class Fields (
        @SerializedName("id") val id : String
    )

    data class Records(
        @SerializedName("관리번호") val manageNumber: String,
        @SerializedName("공원명") val parkName: String,
        @SerializedName("공원구분") val parkCategory: String,
        @SerializedName("소재지도로명주소") val addressDoro: String,
        @SerializedName("소재지지번주소") val addressJibun: String,
        @SerializedName("위도") val latitude: String,
        @SerializedName("경도") val longitude: String,
        @SerializedName("공원면적") val parkSize: String,
        @SerializedName("공원보유시설(운동시설)") val facilityHealth: String,
        @SerializedName("공원보유시설(유희시설)") val facilityJoy: String,
        @SerializedName("공원보유시설(편익시설)") val facilityUseFul: String,
        @SerializedName("공원보유시설(교양시설)") val facilityCulture: String,
        @SerializedName("공원보유시설(기타시설)") val facilityEtc: String,
        @SerializedName("지정고시일") val dateDecision: String,
        @SerializedName("관리기관명") val institutionName1: String,
        @SerializedName("전화번호") val phoneNumber: String,
        @SerializedName("데이터기준일자") val dateReference: String,
        @SerializedName("제공기관코드") val institutionCode: String,
        @SerializedName("제공기관명") val institutionName2: String
    ):Comparable<Records> {
        override fun compareTo(other: Records):Int {
            return compareValuesBy(this, other, { it.latitude.toDouble() }, { it.longitude.toDouble() })
        }
    }
}