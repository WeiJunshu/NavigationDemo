package com.example.navigationdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amap.api.maps.model.LatLng
import com.example.navigationdemo.MyApp
import com.example.navigationdemo.R
import com.example.navigationdemo.model.LocationData
import com.example.navigationdemo.model.NavigationData
import com.example.navigationdemo.repository.LocationRepository
import com.example.navigationdemo.repository.NaviRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRepo: LocationRepository,
    private val naviRepo: NaviRepository
) : ViewModel() {

    val currentLatLng: LiveData<LocationData> = locationRepo.currentLatLng
    val destinationLatLng: LiveData<LocationData> = locationRepo.destinationLatLng
    val naviData: LiveData<NavigationData> = naviRepo.naviData
    private val _tip = MutableLiveData<String>()
    val tip:LiveData<String> = _tip

    fun startLocationUpdates() {
        locationRepo.startLocationUpdates()
    }

    fun stopLocationUpdates(){
        locationRepo.stopLocationUpdates()
    }

    fun selectDestination(dest: LatLng) {
        locationRepo.selectDestination(dest)
    }

    fun startNavigation() {
        val from = currentLatLng.value
        val to = destinationLatLng.value
        if(from==null){
            locationRepo.startLocationUpdates()
            _tip.value= MyApp.instance.resources.getString(R.string.no_location_tip)
            return
        }
        if(to == null){
            _tip.value= MyApp.instance.resources.getString(R.string.select_destination_tip)
            return
        }
        naviRepo.startNavigation(from, to)
    }

    fun release() {
        naviRepo.release()
    }
}
