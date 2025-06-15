package com.example.navigationdemo.repository

import androidx.lifecycle.LiveData
import com.amap.api.maps.model.LatLng
import com.example.navigationdemo.model.LocationData

interface LocationRepository {

    val currentLatLng: LiveData<LocationData>
    val destinationLatLng: LiveData<LocationData>
    fun startLocationUpdates()
    fun stopLocationUpdates()
    fun selectDestination(latLng: LatLng)

    fun release()
}
