package com.example.walkingpark.di.module

import android.content.Context
import androidx.room.Room
import com.example.walkingpark.data.enum.Common
import com.example.walkingpark.data.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    // DB 에 값이 초기화 되었는지 여부를 체크하여, 초기화가 되지 않은 경우 생성하기 위함.
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

}