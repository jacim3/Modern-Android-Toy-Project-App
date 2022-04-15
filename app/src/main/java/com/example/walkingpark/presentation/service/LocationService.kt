package com.example.walkingpark.presentation.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.walkingpark.R
import com.example.walkingpark.constants.Common
import com.example.walkingpark.constants.Settings
import com.example.walkingpark.presentation.MainActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
* 바인드된 서비스로서, 위치데이터를 제공하는 데이터소스로서 정의.
* 최초 bind 이후, mainViewModel 로 부터 locationCallGBack 를 넘겨받으므로, 이러한 의존성을 제거하는 것이
* 추후 과제 -> mainViewModel 에서 LocationCallback 수행을 위하여.
**/

@AndroidEntryPoint
class LocationService : LifecycleService() {

    private val binder = LocalBinder()

    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var serviceHandler: Handler

    var userAddressMap = HashMap<Char,String?>()

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        startForeground(2, setForegroundNotification(this))
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.getStringExtra("intent-filter")) {
            Common.REQUEST_LOCATION_INIT -> {
                startLocationInit(this)
                sendBroadcast(Intent().apply { action = Common.REQUEST_LOCATION_UPDATE_START })
            }

            Common.REQUEST_LOCATION_UPDATE_START -> {
                startLocationUpdate(this)
                sendBroadcast(Intent().apply { action })
            }

            Common.REQUEST_LOCATION_UPDATE_CANCEL -> {

            }
        }
        /*
            1. START_STICKY = Service 가 재시작될 때 null intent 전달
            2. START_NOT_STICKY = Service 가 재시작되지 않음
            3. START_REDELIVER_INTENT = Service 가 재시작될 때 이전에 전달했던 intent 전달
        */
        return super.onStartCommand(intent, flags, START_NOT_STICKY)
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // smallestDisplacement = SMALLEST_DISPLACEMENT_100_METERS // 100 meters
        locationRequest = LocationRequest.create().apply {
            interval = Settings.LOCATION_UPDATE_INTERVAL
            fastestInterval = Settings.LOCATION_UPDATE_INTERVAL_FASTEST
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
         locationCallback =
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    userLocation.postValue(LatLng(result.lastLocation.latitude, result.lastLocation.longitude))
                }
            }

/*        locationCallback =
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)

                    val coder = Geocoder(this@LocationService, Locale.getDefault())

                    // TODO Stream 의 ForEach 와 ForLoop 는 다르며, ForEach 의 리소스 낭비가 심하다.
                    // TODO MutableLiveData 에서는 Null 이 발생할 경우 예외처리가 발생 -> NullCheck 가 엄격한것 같음.
                    // -> Filter 를 통하여 제한한다 하여도 루프를 모두 수행.
                    val location =
                        coder.getFromLocation(
                            result.lastLocation.latitude,
                            result.lastLocation.longitude,
                            Settings.LOCATION_ADDRESS_SEARCH_COUNT
                        )

                    userLocation.postValue(LatLng(result.lastLocation.latitude, result.lastLocation.longitude))
                    userAddressMap = locationServiceInteractor.getAddressFromLocation(location)!!
                    Log.e("asdfasdfasfasd", userAddressMap.toString())
                }

                override fun onLocationAvailability(response: LocationAvailability) {
                    super.onLocationAvailability(response)
                }
            }*/

        userLocation.observe(this){

        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationInit(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("ParkMapsService::class", "퍼미션 허용 안됨")
            return
        } else {
            val src = CancellationTokenSource()
            val ct: CancellationToken = src.token
            fusedLocationProviderClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                ct
            ).addOnFailureListener {
                Log.e("fusedLocationProvider", "fail")
            }.addOnSuccessListener {
                Log.e("fusedLocationProvider", "${it.latitude} ${it.longitude}")

                // parsingAddressMap(context, it.latitude, it.longitude)

            }
        }
    }

    fun getLocationCallback(locationCallback: LocationCallback) {
        this.locationCallback = locationCallback
    }

    // 주기적인 위치 업데이트 수행
    @SuppressLint("MissingPermission")
    private fun startLocationUpdate(
        context: Context
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

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        ).addOnCompleteListener {
            Log.e("LocationServiceRepository : ", "LocationUpdateCallbackRegistered.")
        }
    }

    private fun stopLocationUpdate(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    // 포그라운드 서비스에 필요한 UI 인 Notification 설정 메서드.
    private fun setForegroundNotification(context: Context): Notification {

        val locationTrackNotification = NotificationCompat.Builder(context, "default").apply {
            setContentTitle(Common.DESC_TITLE_LOCATION_NOTIFICATION)
            setContentText(Common.DESC_TEXT_LOCATION_NOTIFICATION)
            setSmallIcon(R.drawable.ic_launcher_foreground)
        }

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

    companion object{
        val userLocation =  MutableLiveData<LatLng?>()
    }


    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService
    }
}