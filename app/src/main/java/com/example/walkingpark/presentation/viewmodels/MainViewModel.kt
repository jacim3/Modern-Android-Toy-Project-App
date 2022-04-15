package com.example.walkingpark.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.walkingpark.domain.model.AirDTO
import com.example.walkingpark.domain.model.StationDTO
import com.example.walkingpark.domain.model.WeatherDTO
import com.example.walkingpark.data.enum.Settings
import com.example.walkingpark.data.repository.LocationReceiverRepository
import com.example.walkingpark.presentation.view.LoadingIndicator
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
// 1. LocationCallback 의 lazy 초기화에 사용될 Context 데이터를 필요로 하여, AndroidViewModel() 사용
// 2. 위치정보 이용을 위하여 필요한 FusedLocationProviderClient, LocationRequest 은 LocationModule 에서 DI 를 통하여 제공
// 3. 예외적으로 locationCallback 은 위치정보 업데이트 시 호출되는 콜백함수며, 여기에서 사용할 비즈니스 로직을
// ViewModel 에서 관리하기 위해 ViewModel 에서 lazy 를 통하여 초기화하며 이를 LocationRepository 에 전달.
class MainViewModel @Inject constructor(
    application: Application,
    private val receiverRepository: LocationReceiverRepository

    ) : AndroidViewModel(application) {

    // TODO 임시. 데이터를 받아와 곧바로 DataBinding 을 통하여 출력. -> 나중에 UI 에 맞추어 수정 필요.
    // 1. 맨 처음 위치정보에 따른 위경도와 GeoCoder 를 통하여 받는 주소정보를 가공.
    // 2. 위의 데이터를 받아옴에 따라, 이를 통하여 미세먼지 측정소 Api 를 통하여 측정소 정보를 얻음.
    // 3. 2번에서 측정소 정보를 받아, 해당 측정소를 기준으로 미세먼지 정보를 받는다.
    // 4.

    val userLiveHolderLatLng = MutableLiveData<LatLng>()
    val userLiveHolderAddress = MutableLiveData<HashMap<Char, String?>>()
    val userLiveHolderStation = MutableLiveData<StationDTO.Response.Body.Items>()
    val userLiveHolderAir = MutableLiveData<List<AirDTO.Response.Body.Items>?>()
    val userLiveHolderWeather = MutableLiveData<
            List<WeatherDTO.Response.Body.Items.Item>>()

    var isAppInitialized = true

    var loadingIndicator: LoadingIndicator? = null


    // -- 위치 정보 가져오기 서비스 관련 초기화.
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)

    val locationRequest = LocationRequest.create().apply {
        interval = Settings.LOCATION_UPDATE_INTERVAL
        fastestInterval = Settings.LOCATION_UPDATE_INTERVAL_FASTEST
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

 /*   val locationCallback =
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

                userLiveHolderLatLng.postValue(LatLng(latitude, longitude))       // 위도경도는 항상 업데이트

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

*//*                        Log.e("addressMap", addressMap.toString())
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
                        loadingIndicator?.dismissIndicator()*//*
                    }
                }
            }

            override fun onLocationAvailability(response: LocationAvailability) {
                super.onLocationAvailability(response)

            }
        }*/

/*        val parkMapsReceiver by lazy {
            ParkMapsReceiver(
                application,
                fusedLocationProviderClient,
                locationRequest,
                locationCallback
            )
        }*/

    fun getData(): LiveData<String> {
        return receiverRepository.getData()
    }
/*    fun registerServiceAndLocationCallback() {
        locationServiceRepository.locationCallback = locationCallback
    }*/

/*    fun cancelUpdateLocation(locationCallback: LocationCallback) {
        locationServiceRepository.fusedLocationClient.removeLocationUpdates(locationCallback)
    }*/

/*    fun getParkMapsService(service: IBinder): ParkMapsService {

        val mb: ParkMapsService.LocalBinder = service as ParkMapsService.LocalBinder
        return mb.getService() // 서비스가 제공하는 메소드 호출하여 서비스쪽 객체를 전달받을수 있슴
}     */

/*    // 실행되는 포그라운드 서비스와 LocationServiceRepository IntentFilter 를 통한 통신을 위한 동적 리시버 정의.
    @AndroidEntryPoint
    class ParkMapsReceiver(val context: Context, private val fusedLocationProviderClient: FusedLocationProviderClient, private val locationRequest: LocationRequest, private val locationCallback: LocationCallback) :
        BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        override fun onReceive(p0: Context?, result: Intent?) {
            Log.e("ParkMapsReceiver", "ParkMapsReceiver")
            when (result!!.action) {
                // 서비스에
                Common.REQUEST_ACTION_UPDATE -> {
//                    val intent = Intent(context, ParkMapsService::class.java)
//                    intent.putExtra("requestCode", Common.LOCATION_UPDATE)
//                    context.startService(intent)
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    ).addOnCompleteListener {
                        Log.e("LocationServiceRepository : ", "LocationUpdateCallbackRegistered.")
                    }
                }
                Common.ACCEPT_ACTION_UPDATE -> {
//                    val addressMap: HashMap<Char, String> =
//                        result.getSerializableExtra("addressMap") as HashMap<Char, String>
//                    //viewModel.userAddressMap.value = addressMap
                }
            }
        }
    }*/
}
