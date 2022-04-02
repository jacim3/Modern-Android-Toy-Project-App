package com.example.walkingpark

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.walkingpark.database.singleton.ParkDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {
    private var handler = Handler(Looper.getMainLooper())

    /**
        res.raw 에 있는 데이터셋 파일을 읽어와 object 클래스에 저장
        TODO 기기 오류로 연산이 안될 수 있고, 이럴경우 현재는 스플래시가 좋료되지 않으므로,
        TODO 이에 대한 예외처리 or timeOut 관련 로직이 필요할 수도...???
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        val intent = Intent(baseContext, MainActivity::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            val job = CoroutineScope(Dispatchers.IO).launch {
                ParkDataSet.setParkDataSet(resources.openRawResource(R.raw.national_park_dataset))
            }
            job.join()
            delay(500)
            startActivity(intent)
            finish()
        }
    }

    companion object{
        const val TIMEOUT_COUNT = 10000
    }
}