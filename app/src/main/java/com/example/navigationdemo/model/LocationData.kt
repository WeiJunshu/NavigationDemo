package com.example.navigationdemo.model

import com.amap.api.maps.model.LatLng

data class LocationData(
    val latLng: LatLng,
    val address: String? = null
)