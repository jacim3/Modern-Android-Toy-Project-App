package com.example.walkingpark.database.singleton

object Common{

    /**
    * 상수값 보관 클래스
    * */
    const val MAIN_PAGER_TAB_NUMBER = 3     // 메인화면 탭 갯수

    const val TIMEOUT_COUNT = 10000         //
    const val SEARCH_LAT_AREA = 0.01        // 1도 = 대략 110.569km
    const val SEARCH_LNG_AREA = 0.01        // 1도 = 대략 111.322km

    const val DATABASE_NAME = "ParkDB"
    const val DATABASE_DIR = "parkdb.db"
}
