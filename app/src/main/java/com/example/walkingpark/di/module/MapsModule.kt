package com.example.walkingpark.di.module

import android.content.Context
import android.location.Geocoder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.*

@Module
@InstallIn(SingletonComponent::class)
object MapsModule {

    @Provides
    fun provideGeocoding(@ApplicationContext context: Context) = Geocoder(context, Locale.getDefault())
}