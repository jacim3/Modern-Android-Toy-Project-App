package com.example.walkingpark.constants

enum class Enums(){}

/**
*   GoogleMaps 의 GeoCoder 의 주소변환 기능 로직 수행을 위한 Enum 클래스
**/
enum class ADDRESS(val text:Char
) {
    DO('도'),
    SI('시'),
    GUN('군'),
    GU('구'),
    EUP('읍'),
    MUN('면'),
    DONG('동')
}

/**
*   동네예보 조회 API 에서 받아온 데이터를 파싱하기 위한 Enum 클래스
**/
enum class WEATHER(val code:String, desc:String) {
    RAIN_CHANCE("POP", "강수확률"),
    RAIN_TYPE("PTY", "강수타입"),
    HUMIDITY("REH","습도"),
    SNOW("SNO","강설"),
    SKY("SKY","하늘상태"),
    TEMPERATURE("TMP","기온"),
    TEMPERATURE_LOW("TMN","최저기온"),
    TEMPERATURE_HIGH("TMX","최고기온"),
    WIND_SPEED("WSD","풍속"),
    WIND_SPEED_EW("UUU","동서풍속"),
    WIND_SPEED_NS("VVV","남북풍속"),
    WAVE_HEIGHT("VEC","파도높이")
}

/**
*   미세먼지 API 에서 받아온 데이터를 파싱하기 위한 Enum 클래스
**/
enum class AIR(val code: String) {
    PM25_24HOUR("pm25Value24"),
    UNIFIED_ATMOSPHERE_VALUE("khaiValue"),
    UNIFIED_ATMOSPHERE_GRADE("khaiGrade"),
    GRADE_AH_WHANG_SAN("so2Grade"),
    GRADE_IL_SAN_HWA("coGrade"),
    GRADE_OH_ZONE("o3Grade"),
    GRADE_E_SAN_HWA("no2Grade"),
    GRADE_PM10_24HOUR("pm10Grade"),
    GRADE_PM25_24HOUR("pm25Grade"),
    GRADE_PM10_01HOUR("pm10Grade1h"),
    GRADE_PM25_01HOUR("pm25Grade1h"),
    FLAG_AH_WHANG_SAN("so2Flag"),
    FLAG_IL_SAN_HWA("coFlag"),
    FLAG_OH_ZONE("o3Flag"),
    FLAG_E_SAN_HWA("no2Flag"),
    FLAG_PM10("pm10Flag"),
    FLAG_PM25("pm25Flag"),
}