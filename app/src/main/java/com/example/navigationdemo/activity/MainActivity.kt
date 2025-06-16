package com.example.navigationdemo.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.maps.model.Poi
import com.amap.api.navi.AmapNaviPage
import com.amap.api.navi.AmapNaviParams
import com.amap.api.navi.AmapNaviType
import com.amap.api.navi.AmapPageType
import com.example.navigationdemo.R
import com.example.navigationdemo.activity.base.BasePermissionActivity
import com.example.navigationdemo.databinding.MainActivityLayoutBinding
import com.example.navigationdemo.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BasePermissionActivity() {
    private lateinit var binding: MainActivityLayoutBinding
    private var mapView: MapView? = null
    private val viewModel: MainViewModel by viewModels()
    private var lastDestinationMarker: Marker? = null
    private var isFirstUpdate: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity_layout)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)
        initView()
        initObserve()
    }

    private fun initView() {
        binding.btNavi.setOnClickListener {
            startNavigation()
        }
    }

    private fun initObserve() {
        viewModel.currentLatLng.observe(this) {
            if (isFirstUpdate) {
                isFirstUpdate = false
                initMap()
            }
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(it.latLng, 17f)
            binding.mapView.map.moveCamera(cameraUpdate)
        }

        viewModel.destinationLatLng.observe(this) {
            lastDestinationMarker?.remove()
            val marker = binding.mapView.map.addMarker(
                MarkerOptions().position(it.latLng).title(it.address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            marker.showInfoWindow()
            lastDestinationMarker = marker
        }
    }

    private fun initMap() {
        binding.mapView.map.apply {
            uiSettings.isZoomControlsEnabled = false
            uiSettings.isScaleControlsEnabled = false
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isGestureScaleByMapCenter = true
            isMyLocationEnabled = true
            myLocationStyle = MyLocationStyle()
                .myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
                .interval(3000)
            setOnMapClickListener {
                viewModel.selectDestination(it)
            }
        }
    }


    private fun startNavigation() {
        val from = viewModel.currentLatLng.value
        if (from == null) {
            Toast.makeText(this, resources.getString(R.string.no_location_tip)
                , Toast.LENGTH_SHORT).show()
            return
        }
        val to = viewModel.destinationLatLng.value
        if (to == null) {
            Toast.makeText(this, resources.getString(R.string.select_destination_tip)
                , Toast.LENGTH_SHORT).show()
            return
        }
        val startPoi = Poi(from.address, from.latLng, from.address)
        val endPoi = Poi(to.address, to.latLng, to.address)
        val params = AmapNaviParams(startPoi, null, endPoi, AmapNaviType.RIDE, AmapPageType.ROUTE)
        AmapNaviPage.getInstance().showRouteActivity(this, params, null, NaviActivity::class.java)
    }

    override fun onPermissionGranted() {
        viewModel.startLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        viewModel.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
        viewModel.release()
    }
}
