package com.example.navigationdemo.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.LatLngBounds
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.maps.model.PolylineOptions
import com.example.navigationdemo.R
import com.example.navigationdemo.databinding.NavigationResultActivityLayoutBinding
import com.example.navigationdemo.model.NavigationData

class NavigationResultActivity : AppCompatActivity() {
    private lateinit var binding: NavigationResultActivityLayoutBinding
    private lateinit var aMap: AMap
    private var navi: NavigationData? = null

    companion object {
        private const val KEY_NAVI_DATA = "key_navi_data"
        private const val TAG = "NavigationResult"
        fun launchActivity(context: Context, data: NavigationData) {
            context.startActivity(Intent(context, NavigationResultActivity::class.java).apply {
                putExtra(KEY_NAVI_DATA, data)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.title = TAG
        binding = DataBindingUtil.setContentView(this, R.layout.navigation_result_activity_layout)
        binding.lifecycleOwner = this
        navi = intent.getParcelableExtra(KEY_NAVI_DATA)
        val mapView: MapView = binding.resultMapView
        mapView.onCreate(savedInstanceState)
        aMap = mapView.map
        aMap.apply {
            uiSettings.isZoomControlsEnabled = false
            uiSettings.isScaleControlsEnabled = false
            isMyLocationEnabled = false
            myLocationStyle = MyLocationStyle()
                .myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
                .interval(3000)
        }
        aMap.addPolyline(PolylineOptions().addAll(navi?.trace?.map {
            LatLng(it.latitude, it.longitude)
        }).width(25f).color(Color.BLUE))
        aMap.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                LatLngBounds.Builder().apply {
                    navi?.trace?.forEach {
                        include(LatLng(it.latitude, it.longitude))
                    }
                }.build(), 100
            )
        )
        binding.durationText = resources.getString(R.string.total_time) + navi?.time.orEmpty()
        binding.distanceText =
            resources.getString(R.string.total_distance) + navi?.distance.orEmpty()
    }

    override fun onResume() {
        super.onResume()
        binding.resultMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.resultMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.resultMapView.onDestroy()
    }
}