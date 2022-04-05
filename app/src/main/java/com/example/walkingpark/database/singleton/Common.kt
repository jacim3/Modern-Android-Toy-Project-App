package com.example.walkingpark.database.singleton
/**
*   상수값 보관 클래스
* */
object Common {
    const val DATABASE_NAME = "ParkDB"      // 데이터베이스 이름
    const val DATABASE_DIR = "parkdb.db"    // 데이터베이스 경로

    // 위치검색 ForeGround Service Notification
    const val DESC_TITLE_LOCATION_NOTIFICATION = "위치 추적"
    const val DESC_TEXT_LOCATION_NOTIFICATION = "사용자의 위치를 확인합니다."

    const val REQUEST_ACTION_UPDATE = "REQUEST_ACTION_UPDATE"
    const val REQUEST_ACTION_PAUSE = "REQUEST_ACTION_PAUSE"

    // Service RequestCode 관련
    const val PERMISSION = 0
    const val LOCATION_UPDATE = 1
    const val LOCATION_UPDATE_CANCEL = 2
    const val LOCATION_SETTINGS = 3
}

object UserData{
    var currentLatitude: Double = 0.0       // 사용자 위도 실시간 저장
    var currentLongitude: Double = 0.0      // 사용자 경도 실시간 저장
    lateinit var userLocation:Array<String> // 사용자 위치정보 저장
}

object Locations{
    // 공공데이터 TM 좌표가 제대로 먹지 않음. -> Geocoder 에서 받아온 주소데이터로 처리.
    const val MAX_LENGTH = 10 // 저장 최대 길이
    const val COUNTRY = 0
    const val SI = 1        // 사
    const val GUN = 2       // 군
    const val GU = 3        // 구
    const val EUP = 4       // ...
    const val DO = 5
    const val MUN = 6
    const val DONG = 7
    const val EX1 = 8       // 여분 주소 넣기
    const val EX2 = 9
    const val EX3 = 10
}

object Settings{
    const val LOCATION_UPDATE_INTERVAL: Long = 10            // 위치 업데이트 간격
    const val LOCATION_UPDATE_INTERVAL_FASTEST: Long = 5     // 위치 업데이트 간격(빠른)
}

object Logic {
    const val TIMEOUT_COUNT = 10000         // 데이터를 요청할 시간
    const val SEARCH_LAT_AREA = 0.01        // 1도 = 대략 110.569km
    const val SEARCH_LNG_AREA = 0.01        // 1도 = 대략 111.322km
}
