package com.example.walkingpark

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.walkingpark.databinding.ActivityMainBinding
import com.example.walkingpark.factory.PublicApiViewModelFactory
import com.example.walkingpark.fragment_tab_1.HomeFragment
import com.example.walkingpark.fragment_tab_2.ParkMapsFragment
import com.example.walkingpark.fragment_tab_3.SettingsFragment
import com.example.walkingpark.repository.PublicDataApiRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import kr.hyosang.coordinate.CoordPoint
import kr.hyosang.coordinate.TransCoord


class MainActivity : AppCompatActivity() {


    private var binding: ActivityMainBinding? = null
    private lateinit var viewModel: MainViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    // TODO 측정소 정보를 가져오려면, 현재 위경도 좌표를 tm 좌표로 변환해야 하며, jar 과 같은 외부 라이브러리는 부정확함.
    // TODO 다른 API 연동이 필요하여 이를 보류.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding!!.lifecycleOwner = this
        viewModel = ViewModelProvider(
            this,
            PublicApiViewModelFactory(PublicDataApiRepository(this))
        )[MainViewModel::class.java]


        // TODO 백그라운드 설정하여 위치정보 얻어오기
        // 1. 퍼미션
        // 2.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        viewModel.serviceStart(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        val src = CancellationTokenSource()
        val ct: CancellationToken = src.token
        fusedLocationClient.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY,
            ct
        ).addOnSuccessListener {
            Log.e("fusedLocationProvider", "${it.latitude} ${it.longitude}")
            val point = CoordPoint(it.latitude, it.longitude)
        }

        binding!!.buttonHome.setOnClickListener {
            val transaction1 = supportFragmentManager.beginTransaction()
            transaction1.replace(R.id.fragmentContainer, HomeFragment()).commit()
        }

        binding!!.buttonMaps.setOnClickListener {
            val transaction2 = supportFragmentManager.beginTransaction()
            transaction2.replace(R.id.fragmentContainer, ParkMapsFragment()).commit()
        }

        binding!!.buttonSettings.setOnClickListener {
            val transaction3 = supportFragmentManager.beginTransaction()
            transaction3.replace(R.id.fragmentContainer, SettingsFragment()).commit()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {

    }
}