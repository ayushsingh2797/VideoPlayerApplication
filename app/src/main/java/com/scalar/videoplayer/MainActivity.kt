package com.scalar.videoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.scalar.videoplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), AdapterListenerInterface {

    lateinit var mainBinding: ActivityMainBinding
    lateinit var videosViewModel: VideosViewModel
    lateinit var videoRVAdapter: VideoRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        mainBinding.rvVideoList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        videosViewModel = ViewModelProviders.of(this).get(VideosViewModel::class.java)
        getVideoList()
        observeLiveData()

    }

    private fun observeLiveData() {
       videosViewModel.videoListLiveData?.observe(this, Observer<VideoModel>{ t->
            if(t!=null){
                setAdapter(t)
            }
       })
    }

    private fun setAdapter(videoModel: VideoModel) {
        val list :List<VideoModel>? =  null
        videoRVAdapter = VideoRVAdapter(this, list!!,this)
        mainBinding.rvVideoList.adapter = videoRVAdapter
    }

    private fun getVideoList() {
        if(UtilityFunctions.isConnectingToInternet(this)){
            videosViewModel.getVideoList()
        }
    }

}