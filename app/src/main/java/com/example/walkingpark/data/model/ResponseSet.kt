package com.example.walkingpark.data.model

import androidx.lifecycle.MutableLiveData
import com.example.walkingpark.data.model.dto.AirDTO
import com.example.walkingpark.data.model.dto.StationDTO
import com.example.walkingpark.data.model.dto.WeatherDTO

data class ResponseSet(

    var station : MutableLiveData<StationDTO.Response.Body.Items?>?,
    var air : MutableLiveData<List<AirDTO.Response.Body.Items>?>?,
    var weather : MutableLiveData<List<WeatherDTO.Response.Body.Items.Item>>?
) {
}