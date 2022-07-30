package com.scalar.videoplayer

import android.content.Context
import android.net.ConnectivityManager

object UtilityFunctions {

    fun isConnectingToInternet(context: Context?): Boolean {
        if (context != null) {
            val connectivity =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connectivity.activeNetworkInfo
            if (info != null) return info.isConnected
            return false
        }
        return false
    }
}