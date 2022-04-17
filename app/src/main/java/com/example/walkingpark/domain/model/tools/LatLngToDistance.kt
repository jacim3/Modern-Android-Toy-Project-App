package com.example.walkingpark.domain.model.tools

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

/**
*   마커 삭제 관련 비즈니스 로직 구현을 위한, 서로 다른 두 점의 위경도를 기준으로 거리를 구하는 클래스.
**/

class LatLngToDistance {
    /*   @JvmStatic
       fun main(args: Array<String>) {

           // 마일(Mile) 단위
           val distanceMile = distance(37.504198, 127.047967, 37.501025, 127.037701, "")

           // 미터(Meter) 단위
           val distanceMeter = distance(37.504198, 127.047967, 37.501025, 127.037701, "meter")

           // 킬로미터(Kilo Meter) 단위
           val distanceKiloMeter = distance(37.504198, 127.047967, 37.501025, 127.037701, "kilometer")
           println(distanceMile)
           println(distanceMeter)
           println(distanceKiloMeter)
       }*/

    /**
     * 두 지점간의 거리 계산
     *
     * @param lat1 지점 1 위도
     * @param lon1 지점 1 경도
     * @param lat2 지점 2 위도
     * @param lon2 지점 2 경도
     * @param unit 거리 표출단위
     * @return
     */
    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: String): Double {
        val theta = lon1 - lon2
        var dist =
            sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(
                deg2rad(theta)
            )
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        if (unit === "km") {
            dist *= 1.609344
        } else if (unit === "m") {
            dist *= 1609.344
        }
        return dist
    }

    // This function converts decimal degrees to radians
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    // This function converts radians to decimal degrees
    private fun rad2deg(rad: Double): Double {
        return rad * 180 / Math.PI
    }
}