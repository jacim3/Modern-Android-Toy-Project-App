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
import com.example.walkingpark.components.ui.dialog.LoadingIndicator
import com.example.walkingpark.data.enum.Common
import com.example.walkingpark.databinding.ActivityMainBinding
import com.example.walkingpark.data.repository.LocationServiceRepository
import com.example.walkingpark.components.ui.fragment.tab_1.HomeFragment
import com.example.walkingpark.components.ui.fragment.tab_2.ParkMapsFragment
import com.example.walkingpark.components.ui.fragment.tab_3.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// TODO 데이터 바인딩 대체?? -> 자세히 알아볼 것 !!!!
// TODO DAGGER 공부 -> 의존주입에 관해 이해
// TODO Coroutine 공부 -> 더욱 확실히 학습!!!!!

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var locationServiceRepository: LocationServiceRepository

    private var binding: ActivityMainBinding? = null
    val viewModel by viewModels<MainViewModel>()

    lateinit var parkMapsReceiver: ParkMapsReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        locationServiceRepository.locationCallback = viewModel.locationCallback
        setBottomMenuButtons()         // 하단 버튼 설정

        parkMapsReceiver = ParkMapsReceiver(applicationContext)
        locationServiceRepository.startParkMapsService(this, parkMapsReceiver)         // 위치데이터 서비스 실행

        // 퍼미션 요청 핸들링. (onActivityResult 대체)
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            viewModel.loadingIndicator = LoadingIndicator(this)
            val check = locationServiceRepository.sendPermissionResultToActivity(this)
            if (check) {
                viewModel.loadingIndicator!!.startLoadingIndicator()
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

/*        viewModel.userLiveHolderStation.observe(this){
            CoroutineScope(Dispatchers.IO).launch {


            }
        }*/
    }

    // 실행되는 포그라운드 서비스와 LocationServiceRepository IntentFilter 를 통한 통신을 위한 동적 리시버 정의.
    @AndroidEntryPoint
    class ParkMapsReceiver(val context: Context) :
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
//                    val addressMap: HashMap<Char, String> =
//                        result.getSerializableExtra("addressMap") as HashMap<Char, String>
//                    //viewModel.userAddressMap.value = addressMap
                }
            }
        }
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
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO 여기서 동적 리시버 해지
        binding = null
    }

}