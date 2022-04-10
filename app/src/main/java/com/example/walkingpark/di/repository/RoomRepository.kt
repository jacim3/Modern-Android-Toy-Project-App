package com.example.walkingpark.di.repository

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.walkingpark.data.enum.ADDRESS
import com.example.walkingpark.data.enum.Common
import com.example.walkingpark.data.room.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepository @Inject constructor() {

    @Inject
    lateinit var appDatabase: AppDatabase

    suspend fun generateDBIfNotExist(@ApplicationContext context: Context) : AppDatabase?{


            val isEmpty = appDatabase.parkDao().checkQuery().isEmpty()
            if (isEmpty) {
                Log.e("DB 생성", "DBDB")
                return Room.databaseBuilder(
                    context, AppDatabase::class.java,
                    Common.LOCAL_DATABASE_NAME
                )
                    .createFromAsset(Common.DATABASE_DIR_PARK_DB)
                    .build()
            }

        return null
    }

    suspend fun getDataFromGridDB(addressMap: HashMap<Char, String>){

        val doName = addressMap[ADDRESS.DO.x]
        val siName = addressMap[ADDRESS.SI.x]
        val gunName = addressMap[ADDRESS.GUN.x]
        val guName = addressMap[ADDRESS.GU.x]
        val dongName = addressMap[ADDRESS.DONG.x]
        val munName = addressMap[ADDRESS.MUN.x]
        val eupName = addressMap[ADDRESS.EUP.x]


    }

    suspend fun allCheck() {

    }
}