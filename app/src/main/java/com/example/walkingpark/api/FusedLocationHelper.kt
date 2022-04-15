package com.example.walkingpark.api

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.example.walkingpark.data.enum.Settings
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FusedLocationHelper (private val context: Context){

    // -- 위치 정보 가져오기 서비스 관련 초기화.
    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    val userLatLng = MutableLiveData<LatLng>()
    private val locationRequest = LocationRequest.create().apply {
        interval = Settings.LOCATION_UPDATE_INTERVAL
        fastestInterval = Settings.LOCATION_UPDATE_INTERVAL_FASTEST
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback =
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                userLatLng.postValue(LatLng(result.lastLocation.latitude, result.lastLocation.longitude))
            }

            override fun onLocationAvailability(response: LocationAvailability) {
                super.onLocationAvailability(response)

            }
        }

    @SuppressLint("MissingPermission")
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
    }
}