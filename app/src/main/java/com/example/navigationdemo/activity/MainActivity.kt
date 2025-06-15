package com.example.navigationdemo.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.MyLocationStyle
import com.example.navigationdemo.R
import com.example.navigationdemo.databinding.MainActivityLayoutBinding
import com.example.navigationdemo.viewmodel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityLayoutBinding
    private var mapView: MapView? = null
    private val viewModel: MapViewModel by viewModels()
    private var lastDestinationMarker: Marker? = null
    private var isFirstUpdate: Boolean = true


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val PACKAGE = "package"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity_layout)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)
        //initView()
        initObserve()
        checkLocationPermission()
    }


    private fun initView() {
        binding.mapView.map.apply {
            uiSettings.isZoomControlsEnabled = false
            uiSettings.isScaleControlsEnabled = false
            isMyLocationEnabled = true
            myLocationStyle = MyLocationStyle()
                .myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
                .interval(3000)
            setOnMapClickListener {
                viewModel.selectDestination(it)
            }
        }
        binding.fab.setOnClickListener {
            moveToCurrentPosition()
        }
    }

    private fun initObserve() {
        viewModel.currentLatLng.observe(this) {
            if (isFirstUpdate) {
                isFirstUpdate = false
                initView()
                moveToCurrentPosition()
            }else{
               CameraUpdateFactory.newLatLngZoom(it.latLng, 16f)
            }
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

        viewModel.naviData.observe(this) {
            it?.let {
                NavigationResultActivity.launchActivity(this@MainActivity, it)
            }
        }

        viewModel.tip.observe(this) {
            Toast.makeText(this, it.orEmpty(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun moveToCurrentPosition() {
        viewModel.currentLatLng.value?.let {
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(it.latLng, 16f)
            binding.mapView.map.moveCamera(cameraUpdate)
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            viewModel.startLocationUpdates()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.startLocationUpdates()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    showPermissionSettingsDialog()
                } else {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.permission_denied),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showPermissionSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.permission_required))
            .setMessage(resources.getString(R.string.manually_turn_on_permission))
            .setPositiveButton(resources.getString(R.string.confirm)) { dialog, which ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts(PACKAGE, packageName, null)
                intent.setData(uri)
                startActivity(intent)
            }
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        viewModel.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
        viewModel.stopLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
        viewModel.release()
    }
}
