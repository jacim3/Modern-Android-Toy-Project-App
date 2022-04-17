package com.example.walkingpark.domain.usecase.api.air.child

import com.example.walkingpark.data.source.api.dto.AirDTO
import com.example.walkingpark.domain.repository.AirApiRepository
import retrofit2.Response
import javax.inject.Inject

class HandleResponseUseCase @Inject constructor(
    private val airRepository: AirApiRepository
) {
    operator fun invoke(response: Response<AirDTO>) = airRepository.handleResponse(response)
}