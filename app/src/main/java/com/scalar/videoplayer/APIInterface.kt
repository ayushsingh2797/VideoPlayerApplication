package com.scalar.videoplayer

import retrofit2.Call
import retrofit2.http.GET

interface APIInterface {

    @GET("bikashthapa01/myvideos-android-app/master/data.json")
    fun getAllVideos() : Call<MainModel>
}