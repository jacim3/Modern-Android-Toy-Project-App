package com.example.walkingpark.di.module

import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.walkingpark.R
import com.example.walkingpark.data.enum.Common
import com.example.walkingpark.data.enum.Settings
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    fun provideFusedLocationProvider(@ApplicationContext context:Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun provideLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest.create().apply {
            interval = Settings.LOCATION_UPDATE_INTERVAL
            fastestInterval = Settings.LOCATION_UPDATE_INTERVAL_FASTEST
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        return locationRequest
    }

    @Provides
    fun provideLocationTrackNotification(@ApplicationContext context: Context): NotificationCompat.Builder {
        val locationTrackNotification = NotificationCompat.Builder(context, "default").apply {
            setContentTitle(Common.DESC_TITLE_LOCATION_NOTIFICATION)
            setContentText(Common.DESC_TEXT_LOCATION_NOTIFICATION)
            setSmallIcon(R.drawable.ic_launcher_foreground)
        }
        return locationTrackNotification
    }

}