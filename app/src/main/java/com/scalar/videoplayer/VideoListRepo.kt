package com.scalar.videoplayer

import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VideoListRepo {

    companion object VideoListRepoSingleton{
        val instance =VideoListRepo()
    }

    private var videoList = MutableLiveData<VideoModel>()

    var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("base URL")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiInterface: APIInterface = retrofit.create(APIInterface::class.java)

    fun fetchVideoSFromAPI():MutableLiveData<VideoModel> {
        val call: Call<VideoModel> = apiInterface.getAllVideos()
        call.enqueue(object : Callback<VideoModel?> {
            override fun onResponse(call: Call<VideoModel?>, response: Response<VideoModel?>) {
                TODO("Not yet implemented")
            }

            override fun onFailure(call: Call<VideoModel?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
        return videoList
    }
}