package com.example.walkingpark

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import java.lang.Exception
import javax.inject.Inject


@AndroidEntryPoint
class Splash : AppCompatActivity() {
    private var handler = Handler(Looper.getMainLooper())

    /**
     *  SingleTon 클래스인 database.room.AppDatabase 의 appDatabase (공원정보 데이터를 참조할 Room 객체) 를 초기화.
     *  공원 데이터를 DB 에 적재해야 하므로, 최초 앱 실행시는 시간이 조금 걸릴 수 있음
     */


    /*
    *
    * .createFromAsset(Common.DATABASE_DIR) .fallbackToDestructiveMigration()*/

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

/*        if (check == "DB 없음") {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(applicationContext, "최초 DB 생성이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            delay(1000)
        }*/
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