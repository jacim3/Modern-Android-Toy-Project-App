package com.example.walkingpark.data.tools

import android.graphics.PointF
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


/**
*   사용자 기준 40도 방향의 위경도 좌표 구하는 클래스.
**/
// param1 : 기준 lat
// param2 : 기준 lng
// param3 : 기준점으로 부터 거리 (km)
class LatLngPoints {
    /**
     * Calculates the end-point from a given source at a given range (meters)
     * and bearing (degrees). This methods uses simple geometry equations to
     * calculate the end-point.
     *
     * @param point
     * Point of origin
     * @param range
     * Range in meters
     * @param bearing
     * Bearing in degrees
     * @return End-point from the source given the desired range and bearing.
     */
    fun calculateDerivedPosition(
        point: PointF,
        range: Double, bearing: Double
    ): PointF {
        val earthRadius = 6371000.0 // m
        val latA = Math.toRadians(point.x.toDouble())
        val lonA = Math.toRadians(point.y.toDouble())
        val angularDistance = range / earthRadius
        val trueCourse = Math.toRadians(bearing)
        var lat = asin(
            sin(latA) * cos(angularDistance) +
                    (cos(latA) * sin(angularDistance)
                            * cos(trueCourse))
        )
        val dlon = atan2(
            sin(trueCourse) * sin(angularDistance)
                    * cos(latA),
            cos(angularDistance) - sin(latA) * sin(lat)
        )
        var lon =
            (lonA + dlon + Math.PI) % (Math.PI * 2) - Math.PI
        lat = Math.toDegrees(lat)
        lon = Math.toDegrees(lon)
        return PointF(lat.toFloat(), lon.toFloat())
    }
}
