package com.example.walkingpark.components.foreground.service

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.walkingpark.data.repository.LocationServiceRepository
import com.example.walkingpark.data.enum.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 *   위치정보 요청 및 업데이트 관련 포그라운드 서비스
 *
 */
@AndroidEntryPoint
class ParkMapsService : Service() {

    private val mBinder: IBinder = LocalBinder()

    var number: Int = 0
        get() = field + 1

    @Inject
    lateinit var locationServiceRepository: LocationServiceRepository

    val thisService: ParkMapsService = this

    class LocalBinder : Binder() {
        fun getService(): ParkMapsService {
            return ParkMapsService().thisService
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("ParkMapsService", "onCreate")

        number = 0
    }

    // 컴포넌트가 서비스에 바인딩하고자 할때 수행
    // 클라이언트가 서비스와 통신을 수고받기 위해 사용할 인터페이스를 여기서 제공해야 한다.
    override fun onBind(sIntent: Intent?): IBinder {
        Log.e("ParkMapsService", "onBind")

        startForeground(2, locationServiceRepository.setLocationTrackNotification(this))
        return mBinder
    }

    // 서비스에 대한 요청이 발생할때마다 호출
    // 컴포넌트가 서비스 사용을 요청할때마다 수행된다.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("ParkMapsService", "onStartCommand()")

        val requestCode = intent!!.getIntExtra("requestCode", -1)

        if (requestCode != -1) {

            when (requestCode) {
                Common.PERMISSION -> {

                    locationServiceRepository.getUserLocationAfterInitFusedLocationProvider(
                        applicationContext
                    )
                    // 서비스의 위치정보 획득이 완료되었음을 알리고, 위치정보 서비스가 초기화가 완료되었음에 따라
                    // 엑티비티에서 다시 서비스에 위치업데이트를 요청하도록 리시버 전송
                    val requestIntent = Intent()
                    requestIntent.action = Common.REQUEST_ACTION_UPDATE
                    sendBroadcast(requestIntent)

                }
                Common.LOCATION_UPDATE -> {
                    locationServiceRepository.setUpdateUserLocation(applicationContext, locationServiceRepository.locationCallback)
                }
                Common.LOCATION_UPDATE_CANCEL -> {
                    locationServiceRepository.cancelUpdateLocation(locationServiceRepository.locationCallback)
                }
                Common.LOCATION_SETTINGS -> {

                }
            }
        }
        /*
            1. START_STICKY = Service 가 재시작될 때 null intent 전달
            2. START_NOT_STICKY = Service 가 재시작되지 않음
            3. START_REDELIVER_INTENT = Service 가 재시작될 때 이전에 전달했던 intent 전달
        */
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    ////
    // fusedLocationClient 객체를 초기화 하며, 사용자 위치정보 찾기 수행
    private fun searchUserLocation() {

        // 서비스의 위치정보 획득이 완료되었음을 알리고, 위치정보 서비스가 초기화가 완료되었음에 따라
        // 엑티비티에서 다시 서비스에 위치업데이트를 요청하도록 리시버 전송
        val requestIntent = Intent()
        requestIntent.action = Common.REQUEST_ACTION_UPDATE
        sendBroadcast(requestIntent)

        // 위치정보 획득이 완료됨에 따라 ViewModel 의 LiveData 업데이트를 위한 요청 수행
        // TODO 작동 안함 !! 다른 방법 필요.
        val acceptIntent = Intent()
        acceptIntent.action = Common.ACCEPT_ACTION_UPDATE
        sendBroadcast(acceptIntent)

    }

    // TODO 구성변경에 따라 변동이 일어날 경우, Bundle에 저장 : 추후 필요할수도???
/*    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }*/
}