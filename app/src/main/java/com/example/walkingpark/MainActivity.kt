package com.example.walkingpark

import android.Manifest
import android.content.*
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.walkingpark.components.foreground.service.ParkMapsService
import com.example.walkingpark.data.enum.Common
import com.example.walkingpark.databinding.ActivityMainBinding
import com.example.walkingpark.di.repository.LocationRepository
import com.example.walkingpark.tabs.tab_1.HomeFragment
import com.example.walkingpark.tabs.tab_2.ParkMapsFragment
import com.example.walkingpark.tabs.tab_3.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// TODO 데이터 바인딩 대체?? -> 자세히 알아볼 것 !!!!
// TODO DAGGER 공부 -> 의존주입에 관해 이해
// TODO Coroutine 공부 -> 더욱 확실히 학습!!!!!

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    //private val viewModel by viewModels<MainViewModel>()            // 뷰모델 주입
    val viewModel by viewModels<MainViewModel>()
    private var isParkMapsServiceRunning = false

    @Inject
    lateinit var locationRepository: LocationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Log.e("mainActivity", viewModel.hashCode().toString())
        locationRepository.locationCallback = viewModel.locationCallback
        setBottomMenuButtons()         // 하단 버튼 설정
        locationRepository.startParkMapsService(this)         // 위치데이터 서비스 실행

        // 퍼미션 요청 핸들링. (onActivityResult 대체)
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val check = locationRepository.sendPermissionResultToActivity(this)
            if (check) {
                // 퍼미션이 허용되었으므로 서비스 실행
                val intent = Intent(this, ParkMapsService::class.java)
                intent.putExtra("requestCode", Common.PERMISSION)
                // 버전별 포그라운드 서비스 실행을 위한 별도의 처리 필요. 오레오 이상은 포그라운드 서비스를 명시해주어야 하는듯
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    startForegroundService(intent)
                else
                    startService(intent)
            } else {
                Toast.makeText(this, "퍼미션을 허용해야 앱 이용이 가능합니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        // 퍼미션 요청 수행!!
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

/*        viewModel.userLocationHolder.observe(this) {
            Log.e("received1", it.toString())
        }

        viewModel.userAddressHolder.observe(this){
            Log.e("received2", it.toString())
        }

        viewModel.userStationHolder.observe(this) {
            CoroutineScope(Dispatchers.IO).launch {
                Log.e("received3",it.stationName)
                viewModel.getAirDataFromApi(it.stationName)
            }
        }*/
    }



    private fun setBottomMenuButtons() {
        // 홈프래그먼트를 기본프래그먼트로 설정
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, HomeFragment()).commit()

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
//        requestLocationUpdate()
    }

    private fun requestLocationUpdate() {
//        val intent = Intent(this, ParkMapsFragment::class.java)
//        intent.putExtra("requestCode", Common.LOCATION_UPDATE)
//        startParkMapsService(intent)
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

    companion object {}
}