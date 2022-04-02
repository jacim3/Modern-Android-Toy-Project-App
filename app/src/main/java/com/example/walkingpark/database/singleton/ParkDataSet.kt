package com.example.walkingpark.database.singleton

import android.util.Log
import com.example.walkingpark.MainActivity
import com.example.walkingpark.R
import com.example.walkingpark.dto.ParkDAO
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter

object ParkDataSet {


    lateinit var globalParkDataSet:ParkDAO
    fun setParkDataSet(jsonStream: InputStream) {
        Log.e("result", "start")

        val writer = StringWriter()
        val buffer = CharArray(1024)

        try {
            val reader = BufferedReader(InputStreamReader(jsonStream, "UTF-8"))
            var n = reader.read(buffer)
            while (n != -1) {
                writer.write(buffer, 0, n)
                n = reader.read(buffer)
            }

        } finally {
            jsonStream.close()
        }
        globalParkDataSet = Gson().fromJson(writer.toString(), ParkDAO::class.java)

//        for (i:Int in result.records.indices) {
//
//        }

        // 데이터에 포함된 \(escaping) 을 모두 무시하고 파싱.
        // val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create()

    }
}