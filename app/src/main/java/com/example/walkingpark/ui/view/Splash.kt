package com.example.walkingpark.ui.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.walkingpark.ui.MainActivity
import com.example.walkingpark.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import java.lang.Exception


@AndroidEntryPoint
class Splash : AppCompatActivity() {
    private var handler = Handler(Looper.getMainLooper())

    /**
     *  SingleTon 클래스인 database.room.AppDatabase 의 appDatabase (공원정보 데이터를 참조할 Room 객체) 를 초기화.
     *  공원 데이터를 DB 에 적재해야 하므로, 최초 앱 실행시는 시간이 조금 걸릴 수 있음
     *  또한 퍼미션을 체크하여, 허용을 하거나, 허용된 이후에만 앱 이용이 가능.
     */

    // TODO 스플래시 체크 메시지
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        CoroutineScope(Dispatchers.IO).launch {
            // parkRoomRepository.generateDBIfNotExist(applicationContext)
            moveToMainActivity()
           // moveToMainActivity()
//            val db = parkDBInstance.build()
//            var check = "DB 있음"
//            if (db.parkDao().checkQuery().isEmpty()) {
//                parkDBInstance.createFromAsset(Common.DATABASE_DIR) .fallbackToDestructiveMigration()
//                check = "DB 없음"
//            }
//            moveToMainActivity(check)
        }
    }

/*    private suspend fun startParkDatabaseJob(): String {

        val count = parkDao.checkQuery().size


        val check by lazy {
            if (count == 0) {
                repository.getInstanceByGenerateDB(applicationContext)
                "DB 없음"
            } else {
                "DB 있음"
            }
        }

        return check
    }*/

    // TODO 체크 비즈니스 로직 작성
    private suspend fun moveToMainActivity() {

        delay(2000)
        val intent = Intent(baseContext, MainActivity::class.java)
        startActivity(intent)
        finish()

    }


    fun sendOtherApi() {
        try {
            val client = OkHttpClient()

        } catch (e: Exception) {
            Log.e("Error", e.printStackTrace().toString())
        }
    }

}