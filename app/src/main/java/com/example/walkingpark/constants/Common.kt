package com.example.walkingpark.constants

import android.annotation.SuppressLint
import java.text.SimpleDateFormat


/**
*   RequestCode 를 위한 구분이나, DI 를 위한 모듈에 사용되며, 비즈니스 로직에는 사용하지 않는 상수 값 정의 클래스
**/
object Common {
    const val LOCAL_DATABASE_NAME = "ParkDB"      // 데이터베이스 이름
    const val DATABASE_DIR_PARK_DB = "parkdb.db"    // 데이터베이스 경로

    // 위치검색 포그라운드 서비스 노티피케이션에 등록할 텍스트
    const val DESC_TITLE_LOCATION_NOTIFICATION = "위치 추적"
    const val DESC_TEXT_LOCATION_NOTIFICATION = "사용자의 위치를 확인합니다."

    const val REQUEST_ACTION_UPDATE = "REQUEST_ACTION_UPDATE"
    const val REQUEST_ACTION_PAUSE = "REQUEST_ACTION_PAUSE"
    const val ACCEPT_ACTION_UPDATE = "ACCEPT_ACTION_UPDATE"

    const val REQUEST_LOCATION_INIT = "REQUEST_LOCATION_INIT"
    const val REQUEST_LOCATION_UPDATE_START = "REQUEST_LOCATION_UPDATE_START"
    const val REQUEST_LOCATION_UPDATE_CANCEL = "REQUEST_LOCATION_UPDATE_CANCEL"

    // Service RequestCode 관련
    const val LOCATION_PERMISSION_PASSED = 0
    const val LOCATION_UPDATE = 1
    const val LOCATION_UPDATE_CANCEL = 2
    const val LOCATION_SETTINGS = 3

    const val BASE_URL_API_AIR = "https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/"
    const val REQUEST_PATH_AIR_API = "getMsrstnAcctoRltmMesureDnsty"

    const val BASE_URL_API_STATION = "https://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/"
    const val REQUEST_PATH_STATION_API = "getMsrstnList"

    const val BASE_URL_API_WEATHER = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"
    const val REQUEST_PATH_WEATHER_API = "getVilageFcst"

    const val LOADING_INDICATOR_DISMISS_TIME = 500

    const val REST_API_DATE_UNIT_FORMAT =  "yyyyMMdd"
    const val REST_API_TIME_UNIT_FORMAT =  "HHmm"

    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat(REST_API_DATE_UNIT_FORMAT)
    @SuppressLint("SimpleDateFormat")
    val timeFormat = SimpleDateFormat(REST_API_TIME_UNIT_FORMAT)
}

/**
*   앱 설정값 관련 상수 설정 클래스
**/
object Settings {
    private const val SECOND: Long = 1000
    private const val MINUTE: Long = SECOND * 60
    private const val HOUR: Long = SECOND * 60

    private const val KILO_METER:Long = 1000

    const val LOCATION_UPDATE_INTERVAL: Long = SECOND * 2       // 위치 업데이트 간격
    const val LOCATION_UPDATE_INTERVAL_FASTEST: Long = SECOND   // 위치 업데이트 간격(빠른)
    const val LOCATION_ADDRESS_SEARCH_COUNT = 5                 // GeoCoding 을 통한 위치 텍스트 검색 갯수

    const val REST_API_REFRESH_INTERVAL = 5 * MINUTE

    // Google Maps 에 임의로 정의한, Zoom 레벨 상수. 0(min) ~ 21(max)
    const val GOOGLE_MAPS_ZOOM_LEVEL_VERY_LOW = 20f
    const val GOOGLE_MAPS_ZOOM_LEVEL_LOW = 17f
    const val GOOGLE_MAPS_ZOOM_LEVEL_DEFAULT = 14f
    const val GOOGLE_MAPS_ZOOM_LEVEL_HIGH = 11f
    const val GOOGLE_MAPS_ZOOM_LEVEL_VERY_HIGH = 8f
}
