package com.example.walkingpark.domain.usecase.api.station.child

import com.example.walkingpark.data.source.api.dto.StationDTO
import com.example.walkingpark.domain.repository.StationApiRepository
import retrofit2.Response
import javax.inject.Inject

class HandleResponseUseCase @Inject constructor(
    private val stationRepository: StationApiRepository
) {
    operator fun invoke(response: Response<StationDTO>) = stationRepository.handleResponse(response)
}