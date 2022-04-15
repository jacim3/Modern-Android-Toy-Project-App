package com.example.walkingpark.di.module

import android.content.Context
import android.content.pm.PackageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object ApiKeyModule {


    // Http 에 항상 감싸서 보내야 하는 RestApi 키
    @Provides
    fun providePublicApiKey(@ApplicationContext context:Context): String {
        try {
            val metaSet = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            );
            if (metaSet.metaData != null) {
                val apiKey = metaSet.metaData.getString("public.data.api.key")
                if (apiKey != null) {

                    return apiKey
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {

        }
        return ""
    }
}