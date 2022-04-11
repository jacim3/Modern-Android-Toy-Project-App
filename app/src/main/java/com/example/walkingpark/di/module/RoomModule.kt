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

    //  DB 인스턴스 생성. 만약, 기존에 assets 파일로 제공되는 DB가 초기화되지 않은 경우, 이를 생성
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