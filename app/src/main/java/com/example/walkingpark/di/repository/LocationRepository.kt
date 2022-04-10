package com.example.walkingpark.di.repository

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import com.example.walkingpark.MainActivity
import com.example.walkingpark.MainViewModel
import com.example.walkingpark.enum.ADDRESS
import com.example.walkingpark.enum.Common
import com.example.walkingpark.enum.Settings
import com.example.walkingpark.enum.UserData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.IndexOutOfBoundsException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor() {

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var locationTrackNotification: NotificationCompat.Builder

    @Inject
    lateinit var locationRequest: LocationRequest

    @Inject
    lateinit var locationCallback: LocationCallback

    val addressMap = HashMap<Char, String?>()

    fun setLocationTrackNotification(@ApplicationContext context: Context): Notification {
        // 위치추적 관련 Notification 생성
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context, 0, notificationIntent, 0)
        }
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

    fun getUserLocationAfterInitFusedLocationProvider(@ApplicationContext context:Context){

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

            UserData.currentLatitude = it.latitude
            UserData.currentLongitude = it.longitude

            getDetailedUserLocation(context, it.latitude, it.longitude)

        }
    }

    fun cancelUpdateLocation(){
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // TODO 지도가 업데이트 됨에 딸, 데이터를 너무 자주 가져오게 되면, 이 데이터를 처리하는데 리소스 낭비 발생
    // TODO 이 앱은 반드시 '한국' 에서만 작동. 도 시 군 구 읍 면 동만 추출.
    // 주소정보를 굳이 가져오는 이유는 공공데이터 api 에서 TM 좌표 조회 기능이 올바르게 작동하지 않음
    // 추가로 동네 예보 정보를 가져오기 위한 주소데이터 필요.
    private fun getDetailedUserLocation(context: Context, latitude: Double, longitude: Double) {

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


            location.map {
                it.getAddressLine(0).toString().split(" ")
            }.flatten().distinct().forEach {
                for (enum in ADDRESS.values()) {
                    if (it[it.lastIndex] == enum.x && addressMap[enum.x] == null) {
                        addressMap[enum.x] = it
                    }
                }
            }

            addressLiveData.value = addressMap
            Log.e("addressMap", addressMap.toString())
            Log.e("addressLivaData", addressLiveData.value.toString())

        } catch (e: IndexOutOfBoundsException) {
            Log.e("IndexOutOfBounn", e.printStackTrace().toString())
        } catch (e: Exception) {
            Log.e("Exception", "")
        }
    }

    // 주기적인 위치 업데이트 수행
    fun setUpdateUserLocation(@ApplicationContext context: Context) {

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
            ).addOnCompleteListener { Log.e("Completed", "Completed") }
        }
    }
}