package com.example.walkingpark

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.walkingpark.components.foreground.service.ParkMapsService
import com.example.walkingpark.data.enum.Common
import com.example.walkingpark.databinding.ActivityMainBinding
import com.example.walkingpark.di.repository.LocationRepository
import com.example.walkingpark.tabs.tab_1.HomeFragment
import com.example.walkingpark.tabs.tab_2.ParkMapsFragment
import com.example.walkingpark.tabs.tab_3.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO 데이터 바인딩 대체?? -> 자세히 알아볼 것 !!!!
// TODO DAGGER 공부 -> 의존주입에 관해 이해
// TODO Coroutine 공부 -> 더욱 확실히 학습!!!!!

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    //private val viewModel by viewModels<MainViewModel>()            // 뷰모델 주입

    val viewModel by viewModels<MainViewModel>()
    lateinit var parkMapsService: ParkMapsService                   // 서비스 객체
    private var isParkMapsServiceRunning = false
    lateinit var parkMapsReceiver: BroadcastReceiver

    @Inject
    lateinit var locationRepository: LocationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Log.e("mainActivity", viewModel.hashCode().toString())
        locationRepository.locationCallback = viewModel.locationCallback
        setBottomMenuButtons()         // 하단 버튼 설정
        startParkMapsService()         // 위치데이터 서비스 실행

        // 퍼미션 요청 핸들링. (onActivityResult 대체)
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            locationRepository.startLocationAfterPermissionCheck(this, parkMapsService)
        }
        // 퍼미션 요청 수행!!
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        viewModel.userLocationHolder.observe(this) {
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
        }
    }

    // 백그라운드 위치정보 서비스 활성 및 콜백 등록.
    private fun startParkMapsService() {

        val serviceConnection: ServiceConnection = object : ServiceConnection {
            // 서비스 연결 관련 콜백
            override fun onServiceConnected(
                name: ComponentName,
                service: IBinder
            ) {
                // 서비스와 연결되었을 때 호출되는 메서드
                // 서비스 객체를 전역변수로 저장
                parkMapsService = viewModel.getParkMapsService(service)
                isParkMapsServiceRunning = true

                // 서비스에서 작업이 완료됨에 따라, 서비스로부터 결과를 수신받을 리시버 등록
                parkMapsReceiver = ParkMapsReceiver(applicationContext, viewModel)
                val filter = IntentFilter().apply {
                    addAction(Common.REQUEST_ACTION_UPDATE)
                    addAction(Common.REQUEST_ACTION_PAUSE)
                    addAction(Common.ACCEPT_ACTION_UPDATE)
                }
                registerReceiver(parkMapsReceiver, filter)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                // 서비스와 연결이 끊겼을 때 호출되는 메서드
                isParkMapsServiceRunning = false
                Toast.makeText(
                    applicationContext,
                    "위치 서비스 연결 해제됨",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        // 서비스 실행
        val intent = Intent(this, ParkMapsService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
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
        requestLocationUpdate()
    }

    private fun requestLocationUpdate() {
        val intent = Intent(this, ParkMapsFragment::class.java)
        intent.putExtra("requestCode", Common.LOCATION_UPDATE)
        startParkMapsService(intent)
    }

    // 동적리시버를 통하여, ParkMapsService 에서 액티비티로 전달되는 로직 정의
    // 서비스에서 최초 위치정보를 성공적으로 받아왔음을 알게되어, 이를 통하여 다시 서비스에 위치업데이트를 요청
    @AndroidEntryPoint
    class ParkMapsReceiver(val context: Context, val viewModel: MainViewModel) :
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
                    val addressMap: HashMap<Char, String> =
                        result.getSerializableExtra("addressMap") as HashMap<Char, String>
                    //viewModel.userAddressMap.value = addressMap
                }
            }
        }
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