package com.example.walkingpark.repository

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import com.example.walkingpark.database.room.AppDatabase
import com.example.walkingpark.database.singleton.Common

class ParkRoomRepository {


    fun getInstanceByExistDB(context: Context) {
        AppDatabase.appDatabase = Room.databaseBuilder(context, AppDatabase::class.java,
            Common.DATABASE_NAME
        )
            .build()
    }

    fun getInstanceByGenerateDB(context: Context){

            AppDatabase.appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, Common.DATABASE_NAME)
                .createFromAsset(Common.DATABASE_DIR)
                .fallbackToDestructiveMigration()
                .build()

    }
}