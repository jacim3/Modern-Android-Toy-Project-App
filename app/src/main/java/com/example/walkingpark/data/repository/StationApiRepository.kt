package com.example.walkingpark.data.repository

import android.location.Address
import com.example.walkingpark.constants.ADDRESS
import com.example.walkingpark.data.source.ApiDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationApiRepository @Inject constructor(
    private val apiDataSource: ApiDataSource
) {

    fun startStationApi(addresses:List<String>) = apiDataSource.getStationApi(getQuery(addresses))

    private fun getQuery(addresses: List<String>): Map<String, String> {

        val addressMap = HashMap<Char, String>()
        addresses.stream().forEach {
            for (enum in ADDRESS.values()) {
                if (it[it.lastIndex] == enum.text && addressMap[enum.text] == null) {
                    addressMap[enum.text] = it
                }
            }
        }
        return getQuery(addressMap)
    }


    private fun getQuery(addressMap: HashMap<Char, String>) = mapOf(
        Pair("returnType", "json"),
        Pair("addr", addressMap[ADDRESS.SI.text]!!.split("시")[0])
    )

}