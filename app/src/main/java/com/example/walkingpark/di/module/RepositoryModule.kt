package com.example.walkingpark.di.module

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

/*    @Binds
    abstract fun bindsAirRepository(impl: AirApiRepositoryImpl): AirApiRepository

    @Binds
    abstract fun bindsStationRepository(impl: StationApiRepositoryImpl): StationApiRepository

    @Binds
    abstract fun bindsWeatherRepository (impl: WeatherApiRepositoryImpl) : WeatherApiRepository

    @Binds
    abstract fun bindsMapsRepository(impl: MapsRepositoryImpl) : MapsRepository*/

}