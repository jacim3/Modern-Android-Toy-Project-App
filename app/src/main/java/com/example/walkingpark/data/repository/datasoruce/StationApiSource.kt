package com.example.walkingpark.data.repository.datasoruce

import com.example.walkingpark.api.PublicApiService
import com.example.walkingpark.domain.model.StationDTO
import com.example.walkingpark.data.enum.ADDRESS
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response
import java.util.stream.Collectors
import kotlin.math.abs

class StationApiSource(
    private val apiKey: String,
    private val api: PublicApiService,
    private val addressMap: Map<Char, String>,
    private val lagLng:LatLng
) {

    suspend fun fetchData(): StationDTO.Response.Body.Items? {
        val query = getQuery()


        val response = api.getStationDataByName(apiKey, query)
        if (response.isSuccessful) {
            return getNearStationByResponse(response)
        }
        return null
    }

    private fun getQuery(): String {
        // TODO 이 때, LivaData = null. 대신, Repository 데이터를 대신 참조
        // TODO 통신장애등 예외사항에 대처하기 위하여 resultCode 를 통한 조건문 작성 필요.
        return addressMap[ADDRESS.SI.x]!!.split("시")[0]
    }

    private fun getNearStationByResponse(response: Response<StationDTO>): StationDTO.Response.Body.Items? {
        if (response.isSuccessful) {
            val data: List<StationDTO.Response.Body.Items> =
                response.body()!!.response.body.items

            val latitude = lagLng.latitude
            val longitude = lagLng.longitude

            // 여러 미세먼지 측정소 결과 중 사용자와 가장 가까운 위치 결과 받아내기.
            val result = data.stream().sorted { p0, p1 ->
                (abs(p0.dmX - latitude) + abs(p0.dmY - longitude))
                    .compareTo(
                        (abs(p1.dmX - latitude) + abs(p1.dmY - longitude))
                    )
            }.collect(Collectors.toList())
            //userLiveHolderStation.postValue(result[0])
            //restApiRepository.userStationItem = result[0]

            return result[0]
        }
        return null
    }
}