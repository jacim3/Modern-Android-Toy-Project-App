package com.example.walkingpark.data.repository

import android.location.Address
import android.util.Log
import com.example.walkingpark.constants.ADDRESS
import com.example.walkingpark.data.source.api.PublicApiService
import com.example.walkingpark.domain.model.StationDTO
import com.example.walkingpark.di.module.PublicDataApiModule
import com.example.walkingpark.domain.repository.StationApiRepository
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response
import java.lang.Exception
import java.util.stream.Collectors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class StationApiRepositoryImpl @Inject constructor(
    private val publicApiKey: String,
    @PublicDataApiModule.StationAPI
    private val stationApi: PublicApiService,
) : StationApiRepository {

    override suspend fun startStationApi(
        query: Map<String, String>,
        latLng: LatLng
    ): Response<StationDTO> {
        return stationApi.getStationDataByName(publicApiKey, query)
    }

    override fun extractQuery(addresses: List<Address>): HashMap<String, String> {

        val addressMap = HashMap<Char, String>()
        addresses.map {
            it.getAddressLine(0).toString().split(" ")
        }.flatten().distinct().forEach {

            for (enum in ADDRESS.values()) {
                if (it[it.lastIndex] == enum.text && addressMap[enum.text] == null) {
                    addressMap[enum.text] = it
                }
            }
        }
        val queryMap = HashMap<String, String>()
        queryMap["returnType"] = "json"
        queryMap["addr"] =  addressMap[ADDRESS.SI.text]!!.split("시")[0]
        return queryMap
    }

    override fun handleResponse(response: Response<StationDTO>): List<StationDTO.Response.Body.Items>? {
        try {
            return response.body()?.response?.body?.items
        } catch (e: Exception) { }
        return null
    }

    override fun extractNearStationByLatLng(items: List<StationDTO.Response.Body.Items>?, latLng: LatLng): StationDTO.Response.Body.Items? {

            if (items.isNullOrEmpty()) {
                return null
            }

            val latitude = latLng.latitude
            val longitude = latLng.longitude

            // 여러 미세먼지 측정소 결과 중 사용자와 가장 가까운 위치 결과 받아내기.
            val result = items.stream().sorted { p0, p1 ->
                (abs(p0.dmX - latitude) + abs(p0.dmY - longitude))
                    .compareTo(
                        (abs(p1.dmX - latitude) + abs(p1.dmY - longitude))
                    )
            }.collect(Collectors.toList())
            Log.e("getNearStationByLatLng : ", result.toString())

            return result[0]

    }
}