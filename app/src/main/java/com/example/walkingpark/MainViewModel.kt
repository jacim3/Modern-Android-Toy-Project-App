package com.example.walkingpark

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkingpark.dto.ParkDTO
import com.example.walkingpark.repository.PublicApiRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: PublicApiRepository) : ViewModel() {

    val parkDataResponse = MutableLiveData<Response<ParkDTO>?>()

    fun getParkDataSet(activity: MainActivity){
        viewModelScope.launch {

            val response = repository.getParkData(activity)
            if (response != null) {

                // TODO 수 많은 데이터 중, 사용자 위치에 가까운 데이터 위주로 필터링 해야 함.
                parkDataResponse.value = response
            }
        }
    }
}