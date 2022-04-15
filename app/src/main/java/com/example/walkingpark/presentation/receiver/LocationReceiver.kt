package com.example.walkingpark.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.example.walkingpark.constants.Common
import com.example.walkingpark.presentation.service.LocationService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationReceiver : BroadcastReceiver() {

    private val mData = MutableLiveData<String>()


    override fun onReceive(context: Context, result: Intent) {

        when (result.action) {
            // 서비스에
            Common.REQUEST_LOCATION_INIT -> {
                // 1. 최초 위치 업데이트 초기화
                //'setLocationInit(context)
                // 2. 위치 초기화 이후, 곧바로 서비스에 위치 업데이트 요청을 보냄.
                val intent = Intent(context, LocationService::class.java)
                intent.putExtra("intent-filter", Common.REQUEST_LOCATION_INIT)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    context.startForegroundService(intent)
                else
                    context.startService(intent)
            }
            Common.REQUEST_LOCATION_UPDATE_START -> {
                val intent = Intent(context, LocationService::class.java)
                intent.putExtra("intent-filter", Common.REQUEST_LOCATION_UPDATE_START)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    context.startForegroundService(intent)
                else
                    context.startService(intent)
            }

            Common.REQUEST_LOCATION_UPDATE_CANCEL -> {

            }
        }
    }

/*    @SuppressLint("MissingPermission")
    private fun setLocationInit(context: Context){
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

    // 주기적인 위치 업데이트 수행
    @SuppressLint("MissingPermission")
    private fun setLocationUpdate(context: Context
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
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            ).addOnCompleteListener {
                Log.e("LocationServiceRepository : ", "LocationUpdateCallbackRegistered.")
            }
        }
    }*/

    fun getLiveData(): MutableLiveData<String> {
        return mData
    }
}