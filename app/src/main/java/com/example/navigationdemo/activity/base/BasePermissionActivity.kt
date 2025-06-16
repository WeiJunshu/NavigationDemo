package com.example.navigationdemo.activity.base

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.navigationdemo.R

open class BasePermissionActivity : AppCompatActivity() {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val PACKAGE = "package"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            onPermissionGranted()
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
                onPermissionGranted()
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

    open fun onPermissionGranted() {

    }
}