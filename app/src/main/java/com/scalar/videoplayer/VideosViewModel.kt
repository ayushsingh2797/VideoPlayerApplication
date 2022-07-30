package com.scalar.videoplayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VideosViewModel:ViewModel() {

    var videoListLiveData : MutableLiveData<VideoModel>? = null
    var videoListRepo:VideoListRepo = VideoListRepo.instance

    fun getVideoList(){
        videoListLiveData =videoListRepo.fetchVideoSFromAPI()
    }

}