package com.example.walkingpark

import android.app.Application
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.walkingpark.components.foreground.service.ParkMapsService
import com.example.walkingpark.components.ui.dialog.LoadingIndicator
import com.example.walkingpark.data.dto.AirDTO
import com.example.walkingpark.data.dto.StationDTO
import com.example.walkingpark.data.dto.WeatherDTO
import com.example.walkingpark.data.enum.ADDRESS
import com.example.walkingpark.data.repository.LocationServiceRepository
import com.example.walkingpark.data.repository.RestApiRepository
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.stream.Collectors
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.math.abs


@HiltViewModel
// 1. LocationCallback 의 lazy 초기화에 사용될 Context 데이터를 필요로 하여, AndroidViewModel() 사용
// 2. 위치정보 이용을 위하여 필요한 FusedLocationProviderClient, LocationRequest 은 LocationModule 에서 DI 를 통하여 제공
// 3. 예외적으로 locationCallback 은 위치정보 업데이트 시 호출되는 콜백함수며, 여기에서 사용할 비즈니스 로직을
// ViewModel 에서 관리하기 위해 ViewModel 에서 lazy 를 통하여 초기화하며 이를 LocationRepository 에 전달.
class MainViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var restApiRepository: RestApiRepository

    @Inject
    lateinit var locationServiceRepository: LocationServiceRepository

    // TODO 임시. 데이터를 받아와 곧바로 DataBinding 을 통하여 출력. -> 나중에 UI 에 맞추어 수정 필요.
    // 1. 맨 처음 위치정보에 따른 위경도와 GeoCoder 를 통하여 받는 주소정보를 가공.
    // 2. 위의 데이터를 받아옴에 따라, 이를 통하여 미세먼지 측정소 Api 를 통하여 측정소 정보를 얻음.
    // 3. 2번에서 측정소 정보를 받아, 해당 측정소를 기준으로 미세먼지 정보를 받는다.
    // 4.
    val userLiveHolderLatLng = MutableLiveData<HashMap<String, Double>>()
    val userLiveHolderAddress = MutableLiveData<HashMap<Char, String?>>()
    val userLiveHolderStation = MutableLiveData<StationDTO.Response.Body.Items>()
    val userLiveHolderAir = MutableLiveData<List<AirDTO.Response.Body.Items>?>()
    val userLiveHolderWeather = MutableLiveData<
            List<WeatherDTO.Response.Body.Items.Item>>()

    var isAppInitialized = true

    var loadingIndicator: LoadingIndicator? = null

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

                userLiveHolderLatLng.postValue(latLngMap)       // 위도경도는 항상 업데이트

                // TODO Location 콜백을 통하여 위경도 업데이트가 될 때, Coroutine 을 통한 비동기 처리.
                // TODO 위치업데이트 콜백이 발생할때마다 Api 통신을 하면 리소스 낭비가 심하므로, 효율적인 Delay 방법 필요
                if (isAppInitialized) {
                    isAppInitialized = false
                    viewModelScope.launch {

                        // 1. 콜백 함수의 결과로, Observer 패턴을 적용하기 위한 liveDataHolder 업데이트

                        val addressMap = locationServiceRepository.parsingAddressMap(
                            application,
                            latitude,
                            longitude
                        )
                        userLiveHolderAddress.postValue(addressMap!!)
                        Log.e("addressMap", addressMap.toString())
                        // 1. 기상정보 가져오기 ( 위,경도 좌표만 얻어오면 수행 가능)
                        val weatherJob = async { getDataFromWeatherAPI() }
                        // 2. 측정소 정보 얻어오기 ( 위,경도 좌표만 얻어오면 수행 가능)
                        // TODO 현재 주소정보를 Geocoder 를 통하여 출력하고 이를 HashMap 형태로 처리하여
                        // TODO 이용하나, 디바이스 언어가 한글이 아닐 경우, 주소정보를 읽지 못하므로, 에러 발생
                        val stationJob = async { getDataFromStationAPI() }

                        // 3. 미세먼지 정보 가져오기 -> 반드시, 측정소 정보를 가져온 이후에 수행 가능.
                        val stationName = stationJob.await()
                        if (stationName != null) {
                            getDataFromAirAPI(stationName.stationName)
                        }
                        weatherJob.await()
                        loadingIndicator?.dismissIndicator()
                    }
                }
            }

            override fun onLocationAvailability(response: LocationAvailability) {
                super.onLocationAvailability(response)

            }
        }
    }


    // TODO 데이터는 모두 올바르게 서버로 보내나, HTTP 500 Internal Server Error 발생.
    // TODO 동네예보 조회서비스는 일단 보류.
    suspend fun getDataFromWeatherAPI() {
        val response = restApiRepository.getWeatherDataByGridXy(
            locationServiceRepository.latLngMap["위도"]!!,
            locationServiceRepository.latLngMap["경도"]!!
        )
        if (response != null && response.isSuccessful) {
            userLiveHolderWeather.postValue(response.body()?.response?.body?.items?.item)
        } else {
            Log.e("통신 실패", "통신실패")
        }
    }

    suspend fun getDataFromStationAPI(): StationDTO.Response.Body.Items? {

        // TODO 이 때, LivaData = null. 대신, Repository 데이터를 대신 참조
        // TODO 통신장애등 예외사항에 대처하기 위하여 resultCode 를 통한 조건문 작성 필요.
        val siName = locationServiceRepository.addressMap[ADDRESS.SI.x]!!.split("시")[0]
        Log.e("11111111", siName)
        val response = restApiRepository.getStationDataBySIName(siName)

        if (response != null) {
            if (response.isSuccessful) {
                val data: List<StationDTO.Response.Body.Items> =
                    response.body()!!.response.body.items

                val latitude = locationServiceRepository.latLngMap["위도"]
                val longitude = locationServiceRepository.latLngMap["경도"]

                // 여러 미세먼지 측정소 결과 중 사용자와 가장 가까운 위치 결과 받아내기.
                val result = data.stream().sorted { p0, p1 ->
                    (abs(p0.dmX - latitude!!) + abs(p0.dmY - longitude!!))
                        .compareTo(
                            (abs(p1.dmX - latitude) + abs(p1.dmY - longitude))
                        )
                }.collect(Collectors.toList())
                userLiveHolderStation.postValue(result[0])
                restApiRepository.userStationItem = result[0]

                return result[0]
            }
        }
        return null
    }

    // 측정소별 디테일한 측정결과를 받기 위해서는 반드시 측정소 정보를 얻어온 후, 미세먼지 측정이 가능하므로,
    // 이 메서드는 observe 를 통하여 수행.
    suspend fun getDataFromAirAPI(stationName: String) {
        val response = restApiRepository.getAirDataByStationName(stationName)

        if (response != null) {

            // TODO 네트워크 통신관련 예외처리 보강 필요!!!
            if (response.isSuccessful) {
                val body = response.body()
                val items = body?.response?.body?.items
                userLiveHolderAir.postValue(items)
            }
        }
    }

    fun cancelUpdateLocation(locationCallback: LocationCallback) {
        locationServiceRepository.fusedLocationClient.removeLocationUpdates(locationCallback)
    }

/*        fun getParkMapsService(service: IBinder): ParkMapsService {

            val mb: ParkMapsService.LocalBinder = service as ParkMapsService.LocalBinder
            return mb.getService() // 서비스가 제공하는 메소드 호출하여 서비스쪽 객체를 전달받을수 있슴
        }*/
}
