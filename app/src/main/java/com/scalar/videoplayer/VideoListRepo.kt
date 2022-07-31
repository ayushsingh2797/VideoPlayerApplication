package com.scalar.videoplayer

import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VideoListRepo {

    companion object{
        val instance:VideoListRepo by lazy {  VideoListRepo() }
    }

    private var videoList = MutableLiveData<List<VideoModelNew>>()

    var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiInterface: APIInterface = retrofit.create(APIInterface::class.java)

    fun fetchVideosFromAPI():MutableLiveData<List<VideoModelNew>> {
        val call: Call<MainModel> = apiInterface.getAllVideos()
        call.enqueue(object : Callback<MainModel?> {
            override fun onResponse(call: Call<MainModel?>, response: Response<MainModel?>) {
                if(response.body() != null) {
                    val data : MainModel = response.body() as MainModel
                    videoList.value = data.categories[0].videos
                }
            }

            override fun onFailure(call: Call<MainModel?>, t: Throwable) {
                videoList.value = null
            }
        })
        return videoList
    }
}