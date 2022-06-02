package com.example.walkingpark.data.model
import com.example.walkingpark.constants.Common

data class ResponseCheck(
    var air:Int? = Common.RESPONSE_INIT,
    var station:Int? = Common.RESPONSE_INIT,
    var weather:Int? = Common.RESPONSE_INIT
)
