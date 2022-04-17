package com.example.walkingpark.domain.repository

import com.example.walkingpark.data.source.room.ParkDB
import com.example.walkingpark.domain.model.MarkerItem
import com.google.android.gms.maps.model.LatLng

interface MapsRepository {

    suspend fun getDatabase(query: Map<String, Double>): HashMap<String, Any>

    fun getDatabaseQuery(latLng: LatLng, cursorValue:Int, mult:Int): HashMap<String, Double>

    fun parsingDatabaseItem(it: ParkDB): MarkerItem
}