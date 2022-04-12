package com.example.walkingpark.data.enum


/**
*   RequestCode 를 위한 구분이나, DI 를 위한 모듈에 사용되며, 비즈니스 로직에는 사용하지 않는 상수 값 정의 클래스
**/
object Common {
    const val LOCAL_DATABASE_NAME = "ParkDB"      // 데이터베이스 이름
    const val DATABASE_DIR_PARK_DB = "parkdb.db"    // 데이터베이스 경로

    // 위치검색 ForeGround Service Notification
    const val DESC_TITLE_LOCATION_NOTIFICATION = "위치 추적"
    const val DESC_TEXT_LOCATION_NOTIFICATION = "사용자의 위치를 확인합니다."

    const val REQUEST_ACTION_UPDATE = "REQUEST_ACTION_UPDATE"
    const val REQUEST_ACTION_PAUSE = "REQUEST_ACTION_PAUSE"
    const val ACCEPT_ACTION_UPDATE = "ACCEPT_ACTION_UPDATE"

    // Service RequestCode 관련
    const val PERMISSION = 0
    const val LOCATION_UPDATE = 1
    const val LOCATION_UPDATE_CANCEL = 2
    const val LOCATION_SETTINGS = 3

    const val BASE_URL_API_AIR = "https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/"
    const val BASE_URL_API_STATION = "https://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/"
    const val BASE_URL_API_WEATHER = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"

    const val LOADING_INDICATOR_DISMISS_TIME = 500
}

/**
*   앱 설정값 관련 상수 설정 클래스
**/
object Settings{
    const val LOCATION_UPDATE_INTERVAL: Long = 3000             // 위치 업데이트 간격
    const val LOCATION_UPDATE_INTERVAL_FASTEST: Long = 1000     // 위치 업데이트 간격(빠른)
    const val LOCATION_ADDRESS_SEARCH_COUNT = 5                 // 현재 위치 LatLng 에 대한 주소 검색 개수
    const val LOCATION_SEARCH_RADIUS = 125.0                    // 위치 검색 반경. 다시 검색할때 해당 수치만큼 증가.

    const val GOOGLE_MAPS_PARK_MARKERS_REFRESH_INTERVAL = 50000 // DB 를 통하여 데이터를 쿼리할 간격 -> 5분

    const val AIR_API_REFRESH_INTERVAL = 100000                  // 미세먼지 10분마다 refresh
    const val STATION_API_REFRESH_INTERVAL = 100000              // 측정소 10분마다 refresh
    const val WEATHER_API_REFRESH_INTERVAL = 100000              // 측정소 10분마다 refresh
}

/**
*   비즈니스 로직 수행 관련 상수값 저장 클래스
*
**/
object Logic {
    const val TIMEOUT_COUNT = 10000         // 데이터를 요청할 시간
    const val SEARCH_LAT_AREA = 0.0025       // 1도 = 대략 110.569km
    const val SEARCH_LNG_AREA = 0.01        // 1도 = 대략 111.322km
    const val SEARCH_AREA_KM = 1.0          // 현재 위치 기준
}
