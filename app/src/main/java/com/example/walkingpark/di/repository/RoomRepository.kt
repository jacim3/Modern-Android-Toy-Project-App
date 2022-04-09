package com.example.walkingpark.di.repository

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.walkingpark.database.room.AppDatabase
import com.example.walkingpark.enum.Common
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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

    suspend fun allCheck() {

            Log.e("aaaaaaaaaaaaa", appDatabase.parkDao().getAll().size.toString())
            Log.e("bbbbbbbbbbbbbbbbbbb", appDatabase.gridDao().getAll().size.toString())

    }
}