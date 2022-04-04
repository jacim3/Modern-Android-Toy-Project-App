package com.example.walkingpark.database.singleton

object Common {

    /**
     * 상수값 보관 클래스
     * */
    val isLocationDataAcquired = false      // 위치정보를 얻어왔음 (=서비스가 올바르게 시작됨) 체크.
    val currnetLatitude: Double = 0.0       // 사용자 위도 저장
    val currentLongitude: Double = 0.0      // 사용자 경도 저장

    const val MAIN_PAGER_TAB_NUMBER = 3     // 메인화면 탭 갯수

    const val TIMEOUT_COUNT = 10000         //
    const val SEARCH_LAT_AREA = 0.01        // 1도 = 대략 110.569km
    const val SEARCH_LNG_AREA = 0.01        // 1도 = 대략 111.322km

    const val DATABASE_NAME = "ParkDB"      // 데이터베이스 이름
    const val DATABASE_DIR = "parkdb.db"    // 데이터베이스 경로

    // 위치검색 ForeGround Service Notification
    const val DESC_TITLE_LOCATION_NOTIFICATION = "위치 추적"
    const val DESC_TEXT_LOCATION_NOTIFICATION = "사용자의 위치를 확인합니다."

    const val LOCATION_UPDATE_INTERVAL:Long = 10000             // 위치 업데이트 간격
    const val LOCATION_UPDATE_INTERVAL_FASTEST:Long = 50000     // 위치 업데이트 간격(빠른)


    // Service RequestCode 관련
    const val PERMISSION = 0
    const val LOCATION_UPDATE = 1
    const val LOCATION_SETTINGS = 2
}
