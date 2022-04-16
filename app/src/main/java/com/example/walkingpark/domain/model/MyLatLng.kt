package com.example.walkingpark.domain.model

/**
    Repository 에서 전달하는 LatLng 객체는 GoogleMaps 에서 위경도 데이터를 저장하기 위해 제공하는 객체.
    다른 플랫폼에서도 오류없이 비즈니스 로직이 동일하게 동작하려면 저장소가 사용하는 객체를 별도로 정의하여 이용해야 함.
*/

data class MyLatLng(
    private val latitude:Double,
    private val longitude: Double
)