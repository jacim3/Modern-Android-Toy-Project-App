package com.example.walkingpark.di.module

import com.example.walkingpark.data.repository.AirApiRepositoryImpl
import com.example.walkingpark.data.repository.MapsRepositoryImpl
import com.example.walkingpark.data.repository.StationApiRepositoryImpl
import com.example.walkingpark.data.repository.WeatherApiRepositoryImpl
import com.example.walkingpark.domain.AirApiRepository
import com.example.walkingpark.domain.MapsRepository
import com.example.walkingpark.domain.StationApiRepository
import com.example.walkingpark.domain.WeatherApiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindsAirRepository(impl: AirApiRepositoryImpl): AirApiRepository

    @Binds
    abstract fun bindsStationRepository(impl: StationApiRepositoryImpl): StationApiRepository

    @Binds
    abstract fun bindsWeatherRepository (impl: WeatherApiRepositoryImpl) : WeatherApiRepository

    @Binds
    abstract fun bindsMapsRepository(impl: MapsRepositoryImpl) : MapsRepository
}