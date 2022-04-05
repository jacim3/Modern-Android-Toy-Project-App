package com.example.walkingpark.components.foreground.service

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.walkingpark.MainActivity
import com.example.walkingpark.R
import com.example.walkingpark.database.singleton.Common
import com.example.walkingpark.database.singleton.Locations
import com.example.walkingpark.database.singleton.Settings
import com.example.walkingpark.database.singleton.UserData
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.hyosang.coordinate.CoordPoint
import kr.hyosang.coordinate.TransCoord
import java.lang.Exception
import java.util.*

/**
 *   위치정보 요청 및 업데이트 관련 포그라운드 서비스
 *
 */
class ParkMapsService : Service() {

    private val mBinder: IBinder = LocalBinder()
    var number: Int = 0
        get() = field + 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationTrackNotification: NotificationCompat.Builder
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    val thisService: ParkMapsService = this

    class LocalBinder : Binder() {
        fun getService(): ParkMapsService {
            return ParkMapsService().thisService
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("ParkMapsService", "onCreate")

        //Log.e("asdfd","onBind()")
        locationTrackNotification = NotificationCompat.Builder(this, "default").apply {
            setContentTitle(Common.DESC_TITLE_LOCATION_NOTIFICATION)
            setContentText(Common.DESC_TEXT_LOCATION_NOTIFICATION)
            setSmallIcon(R.drawable.ic_launcher_foreground)
        }
        CoroutineScope(Dispatchers.Default).launch {

        }
        setLocationRequest()
        setLocationCallback()

        number = 0
    }

    // 컴포넌트가 서비스에 바인딩하고자 할때 수행
    // 클라이언트가 서비스와 통신을 수고받기 위해 사용할 인터페이스를 여기서 제공해야 한다.
    override fun onBind(sIntent: Intent?): IBinder {
        Log.e("ParkMapsService", "onBind")

        // 위치추적 관련 Notification 생성
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }
        locationTrackNotification.setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
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
        startForeground(2, locationTrackNotification.build())
        return mBinder
    }

    // 서비스에 대한 요청이 발생할때마다 호출
    // 컴포넌트가 서비스 사용을 요청할때마다 수행된다.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("ParkMapsService", "onStartCommand()")

        val requestCode = intent!!.getIntExtra("requestCode", -1)

        if (requestCode != -1) {

            when (requestCode) {
                Common.PERMISSION -> {
                    searchLocation()
                }
                Common.LOCATION_UPDATE -> {
                    updateLocation()
                }
                Common.LOCATION_UPDATE_CANCEL -> {
                    stopUpdateLocation()
                }
                Common.LOCATION_SETTINGS -> {

                }
            }
        }
        /*
            1. START_STICKY = Service 가 재시작될 때 null intent 전달
            2. START_NOT_STICKY = Service 가 재시작되지 않음
            3. START_REDELIVER_INTENT = Service 가 재시작될 때 이전에 전달했던 intent 전달
        */
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    // fusedLocationClient 객체를 초기화 하며, 사용자 위치정보 찾기 수행
    private fun searchLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@ParkMapsService)

        if (ActivityCompat.checkSelfPermission(
                this@ParkMapsService,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@ParkMapsService,
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


            // BroadCastReceiver 콜백이 호출되었음을 알리기
            val requestIntent = Intent()
            requestIntent.action = Common.REQUEST_ACTION_UPDATE
            sendBroadcast(requestIntent)

            UserData.currentLatitude = it.latitude
            UserData.currentLongitude = it.longitude
        }
    }

    // TODO 지도가 업데이트 됨에 딸, 데이터를 너무 자주 가져오게 되면, 이 데이터를 처리하는데 리소스 낭비 발생
    // TODO 이 앱은 반드시 '한국' 에서만 작동
    // 주소정보를 굳이 가져오는 이유는 공공데이터 api 에서 TM 좌표 조회 기능이 올바르게 작동하지 않음
    // 추가로 동네 예보 정보를 가져오기 위한 주소데이터 필요.
    private fun getDetailedUserLocation(latitude:Double, longitude:Double){

        // 사용자 위치정보 업데이트!! TM 좌표는 오류가 있움.
        UserData.userLocation = Array(Locations.MAX_LENGTH){"없음"}
        try {
            val coder = Geocoder(this, Locale.getDefault())
            val location = coder.getFromLocation(latitude, longitude, 10)

            for (i in location.indices) {
                val tmp = location[i].getAddressLine(0).split(" ")
                for (j in tmp.indices) {

                    if (!tmp.equals("없음")) {
                        continue
                    }
                    val case = tmp[j]
                    when(case[case.length-1]){
                        '국' -> {
                            UserData.userLocation[Locations.COUNTRY] = case
                        }
                        '시' -> {
                            UserData.userLocation[Locations.SI] = case
                        }
                        '군' -> {
                            UserData.userLocation[Locations.SI] = case
                        }
                        '구' -> {
                            UserData.userLocation[Locations.SI] = case
                        }
                        '읍' -> {
                            UserData.userLocation[Locations.SI] = case
                        }
                        '면' -> {
                            UserData.userLocation[Locations.SI] = case
                        }
                        '동' -> {
                            UserData.userLocation[Locations.SI] = case
                        }
                        else -> {
                            UserData.userLocation[Locations.EX1] = case
                        }

                    }

                }
            }


        } catch (e: Exception) {

        }

            for (i in    UserData.userLocation.indices) {
                Log.e("sadfasdfasdfdsa",    UserData.userLocation[i])
            }

    }

    // 위치 업데이트 요청의 param 으로 사용될 LocationRequest 의 설정객체 초기화
    private fun setLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = Settings.LOCATION_UPDATE_INTERVAL
            fastestInterval = Settings.LOCATION_UPDATE_INTERVAL_FASTEST
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    // 주기적인 위치 업데이트를 요청을 위한 매개변수의 콜백메서드 초기화
    private fun setLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                Log.e("LocationCallback", "OnLocationResult")
                Log.e(
                    "LocationCallback",
                    "${result.lastLocation.latitude} ${result.lastLocation.longitude}"

                )
                UserData.currentLatitude = result.lastLocation.latitude
                UserData.currentLongitude = result.lastLocation.longitude
            }

            override fun onLocationAvailability(response: LocationAvailability) {
                super.onLocationAvailability(response)
                Log.e("LocationCallback", "onLocationAvailability")
                Log.e("LocationCallback", "${response.isLocationAvailable}")
            }
        }
    }

    // 주기적인 위치 업데이트 수행
    private fun updateLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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
            ).addOnCompleteListener { Log.e("Completed", "Completed") }
        }
    }

    // 위치 업데이트 종료
    fun stopUpdateLocation() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // TODO 구성변경에 따라 변동이 일어날 경우, Bundle에 저장 : 추후 필요할수도???
/*    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }*/
}