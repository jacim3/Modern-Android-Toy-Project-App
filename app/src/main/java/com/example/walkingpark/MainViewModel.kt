package com.example.walkingpark

import android.app.Application
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.walkingpark.components.foreground.service.ParkMapsService
import com.example.walkingpark.data.dto.AirDTO
import com.example.walkingpark.data.dto.StationDTO
import com.example.walkingpark.data.enum.ADDRESS
import com.example.walkingpark.di.module.ApiKeyModule
import com.example.walkingpark.di.repository.LocationRepository
import com.example.walkingpark.di.repository.RestApiRepository
import com.example.walkingpark.di.repository.RoomRepository
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashMap
import kotlin.math.abs


@HiltViewModel
class MainViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var restApiRepository: RestApiRepository

    @Inject
    lateinit var locationRepository: LocationRepository

    @Inject
    lateinit var roomRepository: RoomRepository


    val userLocationHolder = MutableLiveData<HashMap<String, Double>>()
    val userAddressHolder = MutableLiveData<HashMap<Char, String?>>()
    val userStationHolder = MutableLiveData<StationDTO.Response.Body.Items>()
    val responseAirDataSet = MutableLiveData<Response<AirDTO>>()
    var isInitialized = true

    @ApiKeyModule.PublicApiKey
    @Inject
    lateinit var a: String

    // TODO LocationRepository 에서 선언되고, Service 에서 수행되는 Location 관련 비즈니스 로직 중
    // TODO Location 콜백함수 객체만 예외로 DI 를 적용하지 않음
    val locationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)

                //UserData.currentLatitude = result.lastLocation.latitude
                //UserData.currentLongitude = result.lastLocation.longitude

                val latitude = result.lastLocation.latitude
                val longitude = result.lastLocation.longitude

                val latLngMap = HashMap<String, Double>()
                latLngMap["위도"] = latitude
                latLngMap["경도"] = longitude

                if (isInitialized) {

                    viewModelScope.launch {


                        // 1. 콜백 함수의 결과로, Observer 패턴을 적용하기 위한 liveDataHolder 업데이트
                        userLocationHolder.postValue(latLngMap)
                        userAddressHolder.postValue(
                            locationRepository.parsingAddressMap(
                                application,
                                latitude,
                                longitude
                            )
                        )
                        // 2. 미세먼지 정보 가져오기 로직 수행
                        // 2.1 측정소 정보 가져오기 로직
                        getStationDataFromApi()
                        // 2.2 가져온 측정소 정보로 미세먼지 정보 가져오기
                        //getAirDataFromApi()
                        // 3. 기상 정보 가져오기 로직 수행

                        // 3.1 DB 를 통하여 좌표 뽑아오기

                        // 3.2 뽑아온 Grid 좌표를 통하여 일기예보 정보 뽑아오기
                        isInitialized = false
                    }
                }
            }

            override fun onLocationAvailability(response: LocationAvailability) {
                super.onLocationAvailability(response)
                Log.e("LocationCallback", "onLocationAvailability")
                Log.e("LocationCallback", "${response.isLocationAvailable}")
            }
        }
    }

    suspend fun getStationDataFromApi(): StationDTO.Response.Body.Items? {

        // TODO 이 때, LivaData = null. 대신, Repository 데이터를 대신 참조
        // TODO 통신장애등 예외사항에 대처하기 위하여 resultCode 를 통한 조건문 작성 필요.
        val siName = locationRepository.addressMap[ADDRESS.SI.x]!!.split("시")[0]
        Log.e("11111111", siName)
        val response = restApiRepository.getStationDataBySIName(siName)

        if (response != null) {
            if (response.isSuccessful) {
                val data: List<StationDTO.Response.Body.Items> =
                    response.body()!!.response.body.items

                val latitude = locationRepository.latLngMap["위도"]
                val longitude = locationRepository.latLngMap["경도"]

                // 여러 미세먼지 측정소 결과 중 사용자와 가장 가까운 위치 결과 받아내기.
                val result = data.stream().sorted { p0, p1 ->
                    (abs(p0.dmX - latitude!!) + abs(p0.dmY - longitude!!))
                        .compareTo(
                            (abs(p1.dmX - latitude) + abs(p1.dmY - longitude))
                        )
                }.collect(Collectors.toList())
                userStationHolder.postValue(result[0])
                restApiRepository.userStationItem = result[0]
            }
        }
        return userStationHolder.value
    }

    // 측정소별 디테일한 측정결과를 받기 위해서는 반드시 측정소 정보를 얻어온 후, 미세먼지 측정이 가능하므로,
    // 이 메서드는 observe 를 통하여 수행.
    suspend fun getAirDataFromApi(stationName: String) {
        val response = restApiRepository.getAirDataByStationName(stationName)

        if (response != null) {

            // TODO 네트워크 통신관련 예외처리 보강 필요!!!
            if(response.isSuccessful) {
                restApiRepository.userAirInfoItem =  response.body()!!.response.body.items
            }
        }
    }

    suspend fun getGridXyFromDatabase() {

    }

    fun getParkMapsService(service: IBinder): ParkMapsService {

        val mb: ParkMapsService.LocalBinder = service as ParkMapsService.LocalBinder
        return mb.getService() // 서비스가 제공하는 메소드 호출하여 서비스쪽 객체를 전달받을수 있슴
    }


    /*    // 위치정보 퍼미션 관련
        // ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION 처리
        fun handleLocationPermissions(permissions : MutableMap<String, Boolean>?): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                when {
                    permissions!!.getOrDefault(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        false
                    ) -> {
                        // Precise location access granted.
                        return true
                    }
                    permissions.getOrDefault(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        false
                    ) -> {
                        // Only approximate location access granted.
                        return true
                    }
                    else -> {
                        // No location access granted.
                        return false
                    }
                }
            }
            return false
        }*/

    /*
        메서드 결과를 감싸서 옵서버패턴 적용 가능
        mainViewModel.getAll().observe(this, Observer {
            textResult.text = it.toString()
        })
    */

}