package com.example.walkingpark.domain.usecase.api.station.child

import com.example.walkingpark.domain.StationApiRepository
import com.example.walkingpark.data.source.api.dto.StationDTO
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response
import javax.inject.Inject

class GetNearStationUseCase @Inject constructor(
    private val stationRepository: StationApiRepository
) {
    operator fun invoke(response: Response<StationDTO>, latLng: LatLng) = stationRepository.extractNearStationByLatLng(response, latLng)
}