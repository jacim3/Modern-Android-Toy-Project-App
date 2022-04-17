package com.example.walkingpark.domain.usecase.maps.db.normal

import com.example.walkingpark.data.source.room.ParkDB
import com.example.walkingpark.domain.repository.MapsRepository
import javax.inject.Inject
/**
*   다른 UseCase 와 의존성 없이 단독으로 수행하는 간단한 작업을 Normal UseCase 로서 정의
*   DB 에서 추출한 아이템의 각각의 튜플에 대하여, 해당 데이터를 Marker 에 사용할 Object 아이템으로 파싱.
**/
class ParsingItemUseCase @Inject constructor(
    private val mapsRepository: MapsRepository
) {
    operator fun invoke(item : ParkDB) = mapsRepository.parsingDatabaseItem(item)
}