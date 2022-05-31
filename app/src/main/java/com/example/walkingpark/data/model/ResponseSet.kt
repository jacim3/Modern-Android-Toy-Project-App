package com.example.walkingpark.data.model

import androidx.lifecycle.MutableLiveData
import com.example.walkingpark.data.model.dto.response.AirResponse
import com.example.walkingpark.data.model.dto.response.StationResponse
import com.example.walkingpark.data.model.dto.response.WeatherResponse

data class ResponseSet(

    var station : MutableLiveData<StationResponse.Response.Body.Items?>?,
    var air : MutableLiveData<List<AirResponse.Response.Body.Items>?>?,
    var weather : MutableLiveData<List<WeatherResponse.Response.Body.Items.Item>>?
) {
}