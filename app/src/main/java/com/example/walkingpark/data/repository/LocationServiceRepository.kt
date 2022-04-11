package com.example.walkingpark.data.repository

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.walkingpark.MainActivity
import com.example.walkingpark.components.foreground.service.ParkMapsService
import com.example.walkingpark.data.enum.ADDRESS
import com.example.walkingpark.data.enum.Common
import com.example.walkingpark.data.enum.Settings
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
*   MainActivity 에서 시작되는 위치검색 ForeGround Service 의 복잡한 비즈니스 로직을 분리
*   Service 는 ViewModel 에서 수행할 경우 컨텍스트 낭비가 생기므로 권장되지 않는듯...??
* */

@Singleton
class LocationServiceRepository @Inject constructor() {

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var locationTrackNotification: NotificationCompat.Builder

    @Inject
    lateinit var locationRequest: LocationRequest

    lateinit var locationCallback: LocationCallback
    val service: ParkMapsService = ParkMapsService()
    val addressMap = HashMap<Char, String?>()
    val latLngMap = HashMap<String, Double>()

    // 실행되는 포그라운드 서비스와 LocationServiceRepository IntentFilter 를 통한 통신을 위한 동적 리시버 정의.
    @AndroidEntryPoint
    class ParkMapsReceiver(val context: Context) :
        BroadcastReceiver() {

        override fun onReceive(p0: Context?, result: Intent?) {
            Log.e("ParkMapsReceiver", "ParkMapsReceiver")
            when (result!!.action) {
                // 서비스에
                Common.REQUEST_ACTION_UPDATE -> {
                    val intent = Intent(context, ParkMapsService::class.java)
                    intent.putExtra("requestCode", Common.LOCATION_UPDATE)
                    context.startService(intent)
                }
                Common.ACCEPT_ACTION_UPDATE -> {
//                    val addressMap: HashMap<Char, String> =
//                        result.getSerializableExtra("addressMap") as HashMap<Char, String>
//                    //viewModel.userAddressMap.value = addressMap
                }
            }
        }
    }

    // 위치정보를 받기 이전, 최초 서비스 시작 요청 메서드
    fun startParkMapsService(context: Context) {

        val serviceConnection: ServiceConnection = object : ServiceConnection {
            // 1. 서비스 연결 관련 콜백 등록
            override fun onServiceConnected(
                name: ComponentName,
                service: IBinder
            ) {
                // 서비스와 연결되었을 때 호출되는 메서드
                // 서비스 객체를 전역변수로 저장
                //parkMapsService = viewModel.getParkMapsService(service)
                //isParkMapsServiceRunning = true

                // 서비스에서 작업이 완료됨에 따라, 서비스로부터 결과를 수신받을 리시버 등록
                val parkMapsReceiver = ParkMapsReceiver(context)
                val filter = IntentFilter().apply {
                    addAction(Common.REQUEST_ACTION_UPDATE)
                    addAction(Common.REQUEST_ACTION_PAUSE)
                    addAction(Common.ACCEPT_ACTION_UPDATE)
                }
                context.registerReceiver(parkMapsReceiver, filter)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                // 서비스와 연결이 끊겼을 때 호출되는 메서드
                //isParkMapsServiceRunning = false
                Toast.makeText(
                    context,
                    "위치 서비스 연결 해제됨",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        // 2. 서비스 실행
        val intent = Intent(context, ParkMapsService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    // 퍼미션이 허용되어 Intent 를 통하여 서비스를 실행할 지 아니면, 앱을 종료할지 체크
    // 서비스는 액티비티에서 실행해야 하므로 이후 로직은 액티비티에서 수행.
    fun sendPermissionResultToActivity(context: Context): Boolean {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) // 퍼미션이 허용되지 않음 -> 종료
        {
            return false
        }
        return true
    }

    // 포그라운드 서비스에 필요한 UI 인 Notification 설정 메서드.
    fun setLocationTrackNotification(context: Context): Notification {
        // 위치추적 관련 Notification 생성
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        locationTrackNotification.setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager: NotificationManager =
                context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
            /*
                1. IMPORTANCE_HIGH = 알림음이 울리고 헤드업 알림으로 표시
                2. IMPORTANCE_DEFAULT = 알림음 울림
                3. IMPORTANCE_LOW = 알림음 없음
                4. IMPORTANCE_MIN = 알림음 없고 상태줄 표시 X
            */
            manager.createNotificationChannel(
                NotificationChannel(
                    "default", "기본 채널",
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
        return locationTrackNotification.build()
    }

    fun getUserLocationAfterInitFusedLocationProvider(context: Context) {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("ParkMapsService::class", "퍼미션 허용 안됨")
        }

        val src = CancellationTokenSource()
        val ct: CancellationToken = src.token
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            ct
        ).addOnFailureListener {
            Log.e("fusedLocationProvider", "fail")
        }.addOnSuccessListener {
            Log.e("fusedLocationProvider", "${it.latitude} ${it.longitude}")

            parsingAddressMap(context, it.latitude, it.longitude)

        }
    }


    // TODO 지도가 업데이트 됨에 딸, 데이터를 너무 자주 가져오게 되면, 이 데이터를 처리하는데 리소스 낭비 발생
    // TODO 이 앱은 반드시 '한국' 에서만 작동. 도 시 군 구 읍 면 동만 추출.
    // 주소정보를 굳이 가져오는 이유는 공공데이터 api 에서 TM 좌표 조회 기능이 올바르게 작동하지 않음
    // 추가로 동네 예보 정보를 가져오기 위한 주소데이터 필요.
    fun parsingAddressMap(
        context: Context,
        latitude: Double,
        longitude: Double
    ): HashMap<Char, String?>? {

        // 사용자 위치정보 업데이트!! TM 좌표는 오류가 있움.

        val addressLiveData =
            MutableLiveData<MutableMap<Char, String?>>()  // 사용자 위치에 대한 주소데이터 저장
        try {
            val coder = Geocoder(context, Locale.getDefault())


            // TODO Stream 의 ForEach 와 ForLoop 는 다르며, ForEach 의 리소스 낭비가 심하다.
            // TODO MutableLiveData 에서는 Null 이 발생할 경우 예외처리가 발생 -> NullCheck 가 엄격한것 같음.
            // -> Filter 를 통하여 제한한다 하여도 루프를 모두 수행.
            val location =
                coder.getFromLocation(latitude, longitude, Settings.LOCATION_ADDRESS_SEARCH_COUNT)

            latLngMap["위도"] = latitude
            latLngMap["경도"] = longitude
            location.map {
                it.getAddressLine(0).toString().split(" ")
            }.flatten().distinct().forEach {
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

    // 주기적인 위치 업데이트 수행
    fun setUpdateUserLocation(
        @ApplicationContext context: Context,
        locationCallback: LocationCallback
    ) {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("ParkMapsService", "퍼미션 허용 안됨")
            return
        }

        CoroutineScope(Dispatchers.Default).launch {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            ).addOnCompleteListener {

                Log.e("LocationServiceRepository : ", "LocationUpdateCallbackRegistered.")
            }
        }
    }

    fun cancelUpdateLocation(locationCallback: LocationCallback) {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
