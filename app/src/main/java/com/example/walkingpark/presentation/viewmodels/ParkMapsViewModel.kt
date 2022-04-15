package com.example.walkingpark.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.walkingpark.data.source.room.ParkDB
import com.example.walkingpark.data.repository.GoogleMapsRepository
import com.example.walkingpark.presentation.view.LoadingIndicator
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParkMapsViewModel @Inject constructor(application: Application, private val googleMapsRepository: GoogleMapsRepository) : AndroidViewModel(application) {


    val loadingIndicator = LoadingIndicator(application, "초기화")
    val liveHolderParkData = MutableLiveData<List<ParkDB>>()
    val liveHolderSeekBar = MutableLiveData<Int>()
    val myLocationLatLng = ObservableField<LatLng>()
    val otherLocationsLatLng = ObservableField<MutableList<LatLng>>()
    init {
        Log.e("init","constructor")
        loadingIndicator.startLoadingIndicator()
    }

    // MainViewModel 의 위경도 업데이트를 제공받아, 위치업데이트를 수행.
    fun getParkData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            // indicator.startLoadingIndicator()
            val response = googleMapsRepository.getParkDataForMaps(latitude, longitude)
            if (response.isNotEmpty()) {
                liveHolderParkData.postValue(response)
            }
            Log.e("parkMapsViewModel", liveHolderParkData.value?.size.toString())
/*            liveHolderParkData.value?.forEach {
                Log.e("Response : ", it.parkName.toString())
            }*/
            //indicator.dismissIndicator()
        }
    }
}