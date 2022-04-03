package com.example.walkingpark

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.walkingpark.database.room.AppDatabase
import com.example.walkingpark.repository.ParkRoomRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {
    private var handler = Handler(Looper.getMainLooper())

    /**
     *  SingleTon 클래스인 database.room.AppDatabase 의 appDatabase (공원정보 데이터를 참조할 Room 객체) 를 초기화.
     *  공원 데이터를 DB 에 적재해야 하므로, 최초 앱 실행시는 시간이 조금 걸릴 수 있음
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        val repository = ParkRoomRepository()

        CoroutineScope(Dispatchers.IO).launch {

            repository.getInstanceByExistDB(applicationContext)
            val count = AppDatabase.appDatabase.parkDao().checkQuery().size
            Log.e("asdf", AppDatabase.appDatabase.parkDao().checkQuery().size.toString())

            var check = "있음"
            if (count == 0) {
                check = "없음"
                repository.getInstanceByGenerateDB(applicationContext)
            }

            Log.e("sadfasdf", count.toString())

            moveToMainActivity(check)
        }
    }

    private suspend fun sortOnlyOneTime() {
        AppDatabase.appDatabase
    }

    private suspend fun moveToMainActivity(check: String) {

        if (check == "없음") {
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


}