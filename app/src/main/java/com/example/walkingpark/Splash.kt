package com.example.walkingpark

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.walkingpark.database.room.AppDatabase
import com.example.walkingpark.repository.ParkRoomRepository
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import java.lang.Exception

class Splash : AppCompatActivity() {
    private var handler = Handler(Looper.getMainLooper())

    /**
     *  SingleTon 클래스인 database.room.AppDatabase 의 appDatabase (공원정보 데이터를 참조할 Room 객체) 를 초기화.
     *  공원 데이터를 DB 에 적재해야 하므로, 최초 앱 실행시는 시간이 조금 걸릴 수 있음
     */

    private val repository = ParkRoomRepository()           // 로컬 DB에 관한 비즈니스 로직 사용을 위한 repository

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        CoroutineScope(Dispatchers.IO).launch {


            // TODO DB 외 추가적으로 수행할 작업은 코루틴으로 병렬처리.
            val databaseJob = async {
                startParkDatabaseJob()
            }

            moveToMainActivity(databaseJob.await())
        }
    }

    private suspend fun startParkDatabaseJob(): String {
        repository.getInstanceByExistDB(applicationContext)
        val count = AppDatabase.appDatabase.parkDao().checkQuery().size

        val check by lazy {
            if (count == 0) {
                repository.getInstanceByGenerateDB(applicationContext)
                "DB 없음"
            } else {
                "DB 있음"
            }
        }
        return check
    }

    private suspend fun moveToMainActivity(check: String) {

        if (check == "DB 없음") {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(applicationContext, "최초 DB 생성이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            delay(1000)
        }

        val intent = Intent(baseContext, MainActivity::class.java)
        startActivity(intent)
        finish()

    }


    fun sendOtherApi() {
        try {
            val client = OkHttpClient()

        }catch (e: Exception) {
            Log.e("Error", e.printStackTrace().toString())
        }
    }

}