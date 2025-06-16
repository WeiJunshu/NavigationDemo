package com.example.navigationdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.amap.api.maps.model.LatLng
import com.example.navigationdemo.model.LocationData
import com.example.navigationdemo.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class MainViewModel @Inject constructor(
    private val locationRepo: LocationRepository,
) : ViewModel() {

    val currentLatLng: LiveData<LocationData> = locationRepo.currentLatLng
    val destinationLatLng: LiveData<LocationData> = locationRepo.destinationLatLng

    fun startLocationUpdates() {
        locationRepo.startLocationUpdates()
    }

    fun selectDestination(dest: LatLng) {
        locationRepo.selectDestination(dest)
    }

    fun release() {
        locationRepo.release()
    }
}
