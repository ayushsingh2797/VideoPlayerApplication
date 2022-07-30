package com.scalar.videoplayer

import retrofit2.Call
import retrofit2.http.GET

interface APIInterface {

    @GET("7c27fa874f0a4d46e4d4")
    fun getAllVideos() : Call<VideoModel>
}