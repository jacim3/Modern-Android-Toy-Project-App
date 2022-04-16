package com.example.walkingpark.presentation

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.walkingpark.R
import com.example.walkingpark.presentation.receiver.LocationReceiver
import com.example.walkingpark.presentation.view.LoadingIndicator
import com.example.walkingpark.constants.Common
import com.example.walkingpark.databinding.ActivityMainBinding
import com.example.walkingpark.presentation.service.LocationService
import com.example.walkingpark.presentation.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job

// TODO 사용자 상호작용에 따른 모든것은 Repository 에서 수행되어선 안된다.
// TODO ViewModel 은 사용자 작업을 처리하고 UI 와 데이터를 모두 통합하는 역할을 수행
// TODO Repository 는 어떤 요청에 따라 데이터를 반환, ViewModel 은 어떤 작업에 반응할지 결정.
// TODO 앱에서 사용할 서비스 및 브로드캐스트 리시버 등록.
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private var binding: ActivityMainBinding? = null
    val viewModel by viewModels<MainViewModel>()

    private var weatherApiJob: Job? = null
    private var stationApiJob: Job? = null
    private lateinit var locationReceiver: LocationReceiver
    private var locationService:LocationService? = null
    // Monitors the state of the connection to the service.

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {

            // 서비스 연결이 완료됨에 따라 MainViewModel 에 생성해 두었던 콜백 등록.
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            // locationService?.getLocationCallback(viewModel.locationCallback)
        }

        override fun onServiceDisconnected(name: ComponentName) {

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setBottomMenuButtons()         // 하단 버튼 설정
        locationReceiver = LocationReceiver()

        val filter = IntentFilter().apply {
            addAction(Common.REQUEST_LOCATION_INIT)
            addAction(Common.REQUEST_LOCATION_UPDATE_START)
            addAction(Common.REQUEST_LOCATION_UPDATE_CANCEL)
        }
        registerReceiver(locationReceiver, filter)

        // 퍼미션 요청 핸들링. (onActivityResult 대체)
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
           // viewModel.loadingIndicator = LoadingIndicator(this, "RestApi 데이터 읽어오는중....")
            val check = permissionCheck(this)
            if (check) {
                viewModel.loadingIndicator!!.startLoadingIndicator()
                // 퍼미션이 허용되었으므로 서비스 실행
                val requestIntent = Intent()
                requestIntent.action = Common.REQUEST_LOCATION_INIT
                sendBroadcast(requestIntent)
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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        // 위치 업데이트 콜백 해지.
        // viewModel.cancelUpdateLocation(viewModel.locationCallback)
        // 포그라운드 서비스 정지.
        val intent = Intent(this, LocationService::class.java)
        stopService(intent)
        unregisterReceiver(locationReceiver)

    }

    // 퍼미션이 허용되어 Intent 를 통하여 서비스를 실행할 지 아니면, 앱을 종료할지 체크
    // 서비스는 액티비티에서 실행해야 하므로 이후 로직은 액티비티에서 수행.
    // TODO 퍼미션 체크 로직을 스플래시로 옮길 예정.
    private fun permissionCheck(context: Context): Boolean {

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

    override fun onStart() {
        super.onStart()

        this.bindService(
            Intent(this, LocationService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    // TODO 서비스는 ViewModel 에서 실행하는것을 지양하므로, 액티비티에서 실행 !!!
    // 위치정보를 받기 이전, 최초 서비스 시작 요청 메서드
    private fun startParkMapsService(context: Context) {

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
    }
}