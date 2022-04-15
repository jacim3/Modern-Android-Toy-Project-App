package com.example.walkingpark.di.module

import android.content.Context
import androidx.room.Room
import com.example.walkingpark.constants.Common
import com.example.walkingpark.data.source.room.AppDatabase
import com.example.walkingpark.data.source.room.ParkDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabaseInstance(@ApplicationContext context: Context): AppDatabase {

        return Room.databaseBuilder(
            context, AppDatabase::class.java,
            Common.LOCAL_DATABASE_NAME
        )
            .createFromAsset(Common.DATABASE_DIR_PARK_DB)
            .build()
    }

    @Provides
    fun provideParkDao(appDatabase: AppDatabase): ParkDao {

        return appDatabase.parkDao()
    }
}