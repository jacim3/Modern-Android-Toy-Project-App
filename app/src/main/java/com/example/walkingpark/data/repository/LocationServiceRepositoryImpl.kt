package com.example.walkingpark.data.repository

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.walkingpark.data.enum.ADDRESS
import com.example.walkingpark.data.enum.Settings
import com.example.walkingpark.domain.usecase.GetAddressMapUseCase
import com.example.walkingpark.domain.LocationServiceRepository
import com.google.android.gms.maps.model.LatLng
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 *   MainActivity 에서 시작되는 위치검색 ForeGround Service 의 복잡한 비즈니스 로직을 분리
 *   Service 는 ViewModel 에서 수행할 경우 컨텍스트 낭비가 생기므로 권장되지 않는듯...??
 **/

@Singleton
class LocationServiceRepositoryImpl @Inject constructor(
    private val addressMapUseCase: GetAddressMapUseCase
) : LocationServiceRepository {

    private var addressMap = HashMap<Char, String?>()
    val userLocation = MutableLiveData<LatLng>()

    override fun getAddressFromLocation(locations: MutableList<Address>): HashMap<Char, String?>? {
        // TODO 지도가 업데이트 됨에 따라, 데이터를 너무 자주 가져오게 되면, 이 데이터를 처리하는데 리소스 낭비 발생
        // TODO 이 앱은 반드시 '한국' 에서만 작동. 도 시 군 구 읍 면 동만 추출.
        // 주소정보를 굳이 가져오는 이유는 공공데이터 api 에서 TM 좌표 조회 기능이 올바르게 작동하지 않음
        // 추가로 동네 예보 정보를 가져오기 위한 주소데이터 필요.


            // 사용자 위치정보 업데이트!! TM 좌표는 오류가 있움.

            try {
                locations.map {
                    it.getAddressLine(0).toString().split(" ")
                }.flatten().distinct().forEach {

                    Log.e("Geocoder : ", it)
                    for (enum in ADDRESS.values()) {
                        if (it[it.lastIndex] == enum.x && addressMap[enum.x] == null) {
                            addressMap[enum.x] = it
                        }
                    }
                }
                return addressMap

            } catch (e: IndexOutOfBoundsException) {
                Log.e("IndexOutOfBounn", e.printStackTrace().toString())
            } catch (e: Exception) {
                Log.e("Exception", "")
            }
        return null
    }

    override fun getLocationCallback(result: Location): LatLng {

        return LatLng(result.latitude, result.longitude)
    }
}
