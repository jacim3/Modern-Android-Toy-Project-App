package com.example.walkingpark.fragment_tab_2

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.walkingpark.MainActivity
import com.example.walkingpark.R
import com.example.walkingpark.dto.ParkDAO
import com.example.walkingpark.dto.ParkDTO
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringWriter

class ParkMapsViewModel : ViewModel() {

    val parkDataHistory = MutableLiveData<List<ParkDAO.Records>>()

    /** deprecated - 공원정보는 json fileset 에서 가져오는것으로 대체
    fun getParkDataSet(){
    viewModelScope.launch {

    val response = repository.getParkData()
    if (response != null) {

    parkDataResponse.value = response
    }
    }
    }
     */


}