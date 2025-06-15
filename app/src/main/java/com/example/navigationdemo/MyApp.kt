package com.example.navigationdemo

import android.app.Application
import com.amap.api.location.AMapLocationClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp:Application(){
    companion object{
      lateinit var instance:MyApp
    }
    override fun onCreate() {
        super.onCreate()
        instance=this
        AMapLocationClient.updatePrivacyShow(this, true, true)
        AMapLocationClient.updatePrivacyAgree(this, true)
    }
}