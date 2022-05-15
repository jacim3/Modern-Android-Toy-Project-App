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

    fun startStationApi(addressSet:List<Address>) = apiDataSource.getStationApi(getQuery(addressSet))

    private fun getQuery(addresses: List<Address>): Map<String, String> {

        val addressMap = HashMap<Char, String>()
        getAddressSet(addresses).stream().forEach {

            for (enum in ADDRESS.values()) {
                if (it[it.lastIndex] == enum.text && addressMap[enum.text] == null) {
                    addressMap[enum.text] = it
                }
            }
        }
        return getStationQuery(addressMap)
    }

    private fun getAddressSet(addresses: List<Address>) = addresses.map {
        it.getAddressLine(0).toString().split(" ")
    }.flatten().distinct()

    private fun getStationQuery(addressMap: HashMap<Char, String>) = mapOf(
        Pair("returnType", "json"),
        Pair("addr", addressMap[ADDRESS.SI.text]!!.split("시")[0])
    )

}