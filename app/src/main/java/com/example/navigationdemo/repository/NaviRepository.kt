package com.example.navigationdemo.repository

import androidx.lifecycle.LiveData
import com.example.navigationdemo.model.LocationData
import com.example.navigationdemo.model.NavigationData

interface NaviRepository {
    val naviData: LiveData<NavigationData>
    fun startNavigation(start: LocationData, end: LocationData)

    fun exitNavigation()

    fun release()
}
