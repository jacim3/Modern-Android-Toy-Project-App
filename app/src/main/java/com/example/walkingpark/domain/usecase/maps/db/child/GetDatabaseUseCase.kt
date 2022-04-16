package com.example.walkingpark.domain.usecase.maps.db.child

import com.example.walkingpark.data.repository.MapsRepositoryImpl
import com.example.walkingpark.domain.MapsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

 class GetDatabaseUseCase @Inject constructor(
     private val mapsRepository: MapsRepository,
     private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend operator fun invoke(query: Map<String, Double>) = withContext(defaultDispatcher) {
        mapsRepository.getDatabase(query)
    }
}