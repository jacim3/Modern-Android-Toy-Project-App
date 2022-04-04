package com.example.walkingpark.repository

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import com.example.walkingpark.database.room.AppDatabase
import com.example.walkingpark.database.singleton.Common

class ParkRoomRepository {


    /**
     * Local DB 에 대한 비즈니스 로직을 보관
     * 변경은 AppDatabase 의 싱글톤 객체인 appDatabase 에 저장되므로, 이를 통하여 접근.
     * */

    fun getInstanceByExistDB(context: Context) {
        AppDatabase.appDatabase = Room.databaseBuilder(context, AppDatabase::class.java,
            Common.DATABASE_NAME
        ).build()
    }

    fun getInstanceByGenerateDB(context: Context){
            AppDatabase.appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, Common.DATABASE_NAME)
                .createFromAsset(Common.DATABASE_DIR)
                .fallbackToDestructiveMigration()
                .build()

    }
}