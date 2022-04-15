package com.example.walkingpark.data.repository.datasoruce

import com.example.walkingpark.api.FusedLocationHelper

class LocationDataSource(private val locationProvider: FusedLocationHelper) {


    fun asdf(){
        locationProvider.userLatLng
    }
}