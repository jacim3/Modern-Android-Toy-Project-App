package com.example.walkingpark.domain.usecase.maps.db

import com.example.walkingpark.data.repository.MapsRepositoryImpl
import com.example.walkingpark.data.source.room.ParkDB
import com.example.walkingpark.domain.MapsRepository
import javax.inject.Inject

class ParsingItemUseCase @Inject constructor(
    private val mapsRepository: MapsRepository
) {
    operator fun invoke(item : ParkDB) = mapsRepository.parsingDatabaseItem(item)
}