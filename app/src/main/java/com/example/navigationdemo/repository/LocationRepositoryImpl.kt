package com.example.navigationdemo.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.example.navigationdemo.model.LocationData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationRepository {
    companion object {
        private const val UNKNOWN = "unknown"
    }

    private val _currentLatLng = MutableLiveData<LocationData>()
    override val currentLatLng: LiveData<LocationData>
        get() = _currentLatLng

    private val _destinationLatLng = MutableLiveData<LocationData>()
    override val destinationLatLng: LiveData<LocationData>
        get() = _destinationLatLng

    private val geocodeSearch = GeocodeSearch(context)

    private val client: AMapLocationClient by lazy {
        AMapLocationClient(context).apply {
            val option = AMapLocationClientOption().apply {
                isOnceLocation = false
                interval = 5000
                locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            }
            setLocationOption(option)
            setLocationListener { loc ->
                Log.i("---Test","code="+loc.errorCode+" errorInfo="+loc.errorInfo)
                if (loc.errorCode == 0) {
                    _currentLatLng.value = LocationData(
                        LatLng(loc.latitude, loc.longitude),
                        address = loc.address
                    )
                }
            }
        }
    }

    override fun startLocationUpdates() {
        client.startLocation()
    }

    override fun stopLocationUpdates() {
        client.stopLocation()
    }

    override fun selectDestination(latLng: LatLng) {
        geocodeSearch.setOnGeocodeSearchListener(object : GeocodeSearch.OnGeocodeSearchListener {
            override fun onRegeocodeSearched(result: RegeocodeResult?, rCode: Int) {
                val address = result?.regeocodeAddress?.formatAddress ?: UNKNOWN
                _destinationLatLng.value = LocationData(latLng, address)
            }

            override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
            }
        })
        geocodeSearch.getFromLocationAsyn(
            RegeocodeQuery(
                LatLonPoint(latLng.latitude, latLng.longitude), 200f, GeocodeSearch.AMAP
            )
        )
    }

    override fun release() {
        client.onDestroy()
    }
}
