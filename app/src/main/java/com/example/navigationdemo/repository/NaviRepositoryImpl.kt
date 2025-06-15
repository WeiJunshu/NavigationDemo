package com.example.navigationdemo.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amap.api.maps.model.Poi
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.AmapNaviPage
import com.amap.api.navi.AmapNaviParams
import com.amap.api.navi.AmapNaviType
import com.amap.api.navi.AmapPageType
import com.example.navigationdemo.listener.NaviSimpleListener
import com.example.navigationdemo.model.LocationData
import com.example.navigationdemo.model.NavigationData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class NaviRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context) :
    NaviSimpleListener(), NaviRepository {

    private val _naviData = MutableLiveData<NavigationData>()
    override val naviData: LiveData<NavigationData>
        get() = _naviData

    private val mapNavi by lazy {
        val instance = AMapNavi.getInstance(context)
        instance.addAMapNaviListener(this@NaviRepositoryImpl)
        instance
    }

    override fun startNavigation(start: LocationData, end: LocationData) {
        mapNavi?.let {
            val startPoi = Poi(start.address, start.latLng, start.address)
            val endPoi = Poi(end.address, end.latLng, end.address)
            val params =
                AmapNaviParams(startPoi, null, endPoi, AmapNaviType.RIDE, AmapPageType.ROUTE)
            AmapNaviPage.getInstance().showRouteActivity(context, params, null)
        }
    }

    override fun exitNavigation() {
        AmapNaviPage.getInstance().exitRouteActivity()
    }

    override fun release() {
        mapNavi.stopNavi()
        AMapNavi.destroy()
    }

    override fun onEndEmulatorNavi() {
        super.onEndEmulatorNavi()
        onArriveDestination()
    }

    override fun onArriveDestination() {
        super.onArriveDestination()
        val totalTimeInSeconds = mapNavi.naviPath.allTime
        val minutes = totalTimeInSeconds / 60
        val seconds = totalTimeInSeconds % 60
        val navi = NavigationData(
            distance = "${mapNavi.naviPath.allLength}m",
            time = "${minutes}m${seconds}s",
            trace = mapNavi.naviPath.coordList ?: emptyList()
        )
        _naviData.value = navi
        MainScope().launch {
            delay(4000)
            exitNavigation()
        }
    }

}
