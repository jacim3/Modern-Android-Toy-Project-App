package com.example.walkingpark.database.singleton

import android.util.Log
import com.example.walkingpark.dto.ParkDAO
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter
import java.util.*

object ParkDataSet {


    lateinit var globalParkDataSet:PriorityQueue<ParkDAO.Records>
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
        // 1. JSON 을 GSON 을 통하여 직렬화한 결과
        val result:MutableList<ParkDAO.Records> = Gson().fromJson(writer.toString(), ParkDAO::class.java).records.toMutableList()
        val heapQueue:PriorityQueue<ParkDAO.Records> = PriorityQueue()


        val n = result.size

        for (i:Int in 0 until n) {
            val latCheck = result[i].latitude.isEmpty()
            val lngCheck = result[i].longitude.isEmpty()
            if (latCheck || lngCheck) continue

            heapQueue.offer(result[i])
        }
        globalParkDataSet = heapQueue

        Log.e("asdf", globalParkDataSet.size.toString())

/*        CoroutineScope(Dispatchers.IO).launch {

        }*/
/*
        CoroutineScope(Dispatchers.IO).launch {

        }*/
        // 2. 힙정렬을 통하여 한번 더 정렬

        // 데이터에 포함된 \(escaping) 을 모두 무시하고 파싱.
        // val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create()

    }
}