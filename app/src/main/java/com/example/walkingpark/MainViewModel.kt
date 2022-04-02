package com.example.walkingpark

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkingpark.dto.AirDTO
import com.example.walkingpark.dto.ParkDTO
import com.example.walkingpark.dto.StationTmDTO
import com.example.walkingpark.repository.PublicDataApiRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: PublicDataApiRepository) : ViewModel() {


    val responseStationTmDataSet = MutableLiveData<Response<StationTmDTO>>()
    val responseAirDataSet = MutableLiveData<Response<AirDTO>>()





}