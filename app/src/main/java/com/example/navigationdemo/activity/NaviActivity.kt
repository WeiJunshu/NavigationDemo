package com.example.navigationdemo.activity

import android.os.Bundle
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.AmapRouteActivity
import com.example.navigationdemo.listener.NaviSimpleListener
import com.example.navigationdemo.model.NaviData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NaviActivity : AmapRouteActivity() {
    private lateinit var mapNavi: AMapNavi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapNavi = AMapNavi.getInstance(application)
        mapNavi.addAMapNaviListener(object : NaviSimpleListener() {
            override fun onArriveDestination() {
                startResultActivity()
            }
        })
    }
    private fun startResultActivity() {
        val totalTimeInSeconds = mapNavi.naviPath.allTime
        val minutes = totalTimeInSeconds / 60
        val seconds = totalTimeInSeconds % 60
        val navi = NaviData(
            distance = "${mapNavi.naviPath.allLength}m",
            time = "${minutes}m${seconds}s",
            trace = mapNavi.naviPath.coordList ?: emptyList()
        )
        MainScope().launch {
            delay(4000)
            NaviResultActivity.launchActivity(this@NaviActivity, navi)
            this@NaviActivity.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapNavi.stopNavi()
        AMapNavi.destroy()
    }
}