package com.example.walkingpark.components.ui.fragment.tab_2

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkingpark.components.ui.dialog.LoadingIndicator
import com.example.walkingpark.data.room.ParkDB
import com.example.walkingpark.data.repository.GoogleMapsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParkMapsViewModel @Inject constructor() : ViewModel() {


    val liveHolderParkData = MutableLiveData<List<ParkDB>>()

    @Inject
    lateinit var googleMapsRepository: GoogleMapsRepository

    fun getParkData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            // indicator.startLoadingIndicator()
            val response = googleMapsRepository.getParkDataForMaps(latitude, longitude)
            if (response.isNotEmpty()) liveHolderParkData.postValue(response)
            Log.e("parkMapsViewModel", liveHolderParkData.value?.size.toString())
/*            liveHolderParkData.value?.forEach {
                Log.e("Response : ", it.parkName.toString())
            }*/
            //indicator.dismissIndicator()
        }
    }
}