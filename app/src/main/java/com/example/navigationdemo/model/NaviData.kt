package com.example.navigationdemo.model

import android.os.Parcelable
import com.amap.api.navi.model.NaviLatLng
import kotlinx.parcelize.Parcelize


@Parcelize
data class NaviData(
    val distance: String?,
    val time: String?,
    val trace:List<NaviLatLng>?=null
):Parcelable