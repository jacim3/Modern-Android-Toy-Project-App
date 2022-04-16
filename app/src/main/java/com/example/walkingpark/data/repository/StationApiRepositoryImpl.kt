package com.example.walkingpark.data.repository

import android.location.Address
import android.util.Log
import com.example.walkingpark.constants.ADDRESS
import com.example.walkingpark.data.source.api.PublicApiService
import com.example.walkingpark.data.source.api.dto.StationDTO
import com.example.walkingpark.data.source.api.dto.WeatherDTO
import com.example.walkingpark.di.module.PublicDataApiModule
import com.example.walkingpark.domain.StationApiRepository
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response
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

    private var responseStationApi: List<WeatherDTO.Response.Body.Items.Item>? = null

    override suspend fun startStationApi(
        query: Map<String, String>,
        latLng: LatLng
    ): Response<StationDTO> {
        val response = stationApi.getStationDataByName(publicApiKey, query)
        Log.e("getDataFromStationApi : ", response.body()?.response?.body?.items?.size.toString())
        return response
    }

    override fun extractQuery(addresses: List<Address>): HashMap<String, String> {

        val addressMap = HashMap<Char, String>()
        addresses.map {
            it.getAddressLine(0).toString().split(" ")
        }.flatten().distinct().forEach {

            for (enum in ADDRESS.values()) {
                if (it[it.lastIndex] == enum.x && addressMap[enum.x] == null) {
                    addressMap[enum.x] = it
                }
            }
        }
        val queryMap = HashMap<String, String>()
        queryMap["returnType"] = "json"
        queryMap["addr"] =  addressMap[ADDRESS.SI.x]!!.split("시")[0]
        return queryMap
    }

    override fun extractNearStationByLatLng(response: Response<StationDTO>, latLng: LatLng): StationDTO.Response.Body.Items? {
        if (response.isSuccessful) {
            val data: List<StationDTO.Response.Body.Items> =
                response.body()!!.response.body.items

            val latitude = latLng.latitude
            val longitude = latLng.longitude

            // 여러 미세먼지 측정소 결과 중 사용자와 가장 가까운 위치 결과 받아내기.
            val result = data.stream().sorted { p0, p1 ->
                (abs(p0.dmX - latitude) + abs(p0.dmY - longitude))
                    .compareTo(
                        (abs(p1.dmX - latitude) + abs(p1.dmY - longitude))
                    )
            }.collect(Collectors.toList())
            Log.e("getNearStationByLatLng : ", result.toString())

            return result[0]
        }
        return null
    }
}