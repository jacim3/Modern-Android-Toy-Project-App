package com.example.walkingpark.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
*       MarkerCluster 를 위한 마커를 생성하기 위한 마커 하나하나의 아이템을 정의하는 객체.
*       ClusterItem 클래스를 상속한 클래스
* */
class MarkerItem(
    private val lat: Double,
    private val lng: Double,
    private val title: String,
    private val snippet: String,
    val size:Float
) :ClusterItem{

    private val position: LatLng = LatLng(lat, lng)

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }
}
