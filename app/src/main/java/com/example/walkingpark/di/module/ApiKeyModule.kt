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


    @PublicApiKey
    @Provides
    fun getMapsApiKey(@ApplicationContext context:Context): String {
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

    @MapsApiKey
    @Provides
    fun getPublicDataApiKey(@ApplicationContext context: Context): String {
        try {
            val metaSet = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            );
            if (metaSet.metaData != null) {
                val apiKey = metaSet.metaData.getString("com.google.android.geo.API_KEY")
                if (apiKey != null) {

                    return apiKey
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {

        }
        return ""
    }

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class PublicApiKey

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class MapsApiKey
}