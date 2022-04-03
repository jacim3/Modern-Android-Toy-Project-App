package com.example.walkingpark.components.background.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.walkingpark.MainActivity
import com.example.walkingpark.R

class ParkMapsService : Service() {

    //    // 지오펜싱
//    lateinit var gpsTracker:GpsTracker
//    override fun onBind(intent: Intent): IBinder {
//
//    }


    // 컴포넌트가 서비스에 바인딩하고자 할때 수행
    // 클라이언트가 서비스와 통신을 수고받기 위해 사용할 인터페이스를 여기서 제공해야 한다.
    override fun onBind(p0: Intent?): IBinder? {

        Log.e("asdfd","onBind()")
//        val notification = NotificationCompat.Builder(this, "default").apply {
//            setContentTitle("Music Player")
//            setContentText("음악이 재생중입니다.")
//            setSmallIcon(R.drawable.ic_launcher_foreground)
//            setContentIntent(pendingIntent)
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val manager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            /*
//            1. IMPORTANCE_HIGH = 알림음이 울리고 헤드업 알림으로 표시
//            2. IMPORTANCE_DEFAULT = 알림음 울림
//            3. IMPORTANCE_LOW = 알림음 없음
//            4. IMPORTANCE_MIN = 알림음 없고 상태줄 표시 X
//            */
//            manager.createNotificationChannel(
//                NotificationChannel("default","기본 채널",
//                    NotificationManager.IMPORTANCE_LOW)
//            )
//        }
//        startForeground(2, notification.build())
//        /*
//        1. START_STICKY = Service 가 재시작될 때 null intent 전달
//        2. START_NOT_STICKY = Service 가 재시작되지 않음
//        3. START_REDELIVER_INTENT = Service 가 재시작될 때 이전에 전달했던 intent 전달
//        */
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    // 서비스 시작 콜백
    // 컴포넌트가 서비스 사용을 요청할때마다 수행된다.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("asdf","onStartCommand()")
        val notificationIntent = Intent(this, MainActivity::class.java)

        return START_STICKY

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}