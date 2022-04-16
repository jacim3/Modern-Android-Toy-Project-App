package com.example.walkingpark.domain.usecase.maps.db.parent

import com.example.walkingpark.domain.usecase.maps.db.child.GetDatabaseQueryUseCase
import com.example.walkingpark.domain.usecase.maps.db.child.GetDatabaseUseCase
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ResultDataBaseUseCase @Inject constructor(
    private val getDatabaseQueryUseCase: GetDatabaseQueryUseCase,
    private val getDatabaseUseCase: GetDatabaseUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend operator fun invoke(latLng: LatLng, cursorValue:Int, mult:Int) = withContext(defaultDispatcher){
        getDatabaseUseCase(getDatabaseQueryUseCase(latLng, cursorValue, mult))
    }
}