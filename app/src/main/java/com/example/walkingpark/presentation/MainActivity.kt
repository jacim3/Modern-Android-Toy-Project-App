package com.example.walkingpark.presentation

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.walkingpark.R
import com.example.walkingpark.constants.Common
import com.example.walkingpark.databinding.ActivityMainBinding
import com.example.walkingpark.presentation.receiver.LocationReceiver
import com.example.walkingpark.presentation.service.LocationService
import com.example.walkingpark.presentation.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO 퍼미션을 체크하고, 위치정보 획득을 위한 서비스
@AndroidEntryPoint
class MainActivity : AppCompatActivity(
) {
    private var binding: ActivityMainBinding? = null
    val viewModel by viewModels<MainViewModel>()

    private lateinit var locationReceiver: LocationReceiver
    private var locationService: LocationService? = null

    // 서비스가 완료되었을 때, 수행되는 콜백
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {

            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            // LocationService 의 Flowable 을 ViewModel 로 전달.
            locationService?.let {
                viewModel.locationObservable.value = it.getLocationFlowable()
                viewModel.locationObservableHandler()
            }
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
            transaction2.replace(R.id.fragmentContainer, MapsFragment()).commit()
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
        ) {
            return false
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        Intent(this, LocationService::class.java).apply {
            startService(this)
            bindService(
                this,
                serviceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }
}