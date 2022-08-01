package com.scalar.videoplayer

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager

object UtilityFunctions {

    fun checkIsNotNull(string: String?): Boolean {
        var flag = false
        flag = (string != null && !string.trim { it <= ' ' }.equals("", ignoreCase = true)
                && !string.trim { it <= ' ' }.equals("null", ignoreCase = true)
                && !string.trim { it <= ' ' }.equals("-", ignoreCase = true))
        return flag
    }

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

    fun setViewsForVideo(context: Context,title:String,views:Int){
        val sharedPreference = context.getSharedPreferences("VIEWS_DATA",Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putInt(title,views)
        editor.apply()
    }

    fun getViewsOfVideos(context: Context,title: String):Int{
        val sharedPreference = context.getSharedPreferences("VIEWS_DATA",Context.MODE_PRIVATE)
        return sharedPreference.getInt(title,0)
    }
}