package com.scalar.videoplayer

import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.scalar.videoplayer.databinding.ActivityMainBinding
import okhttp3.internal.Util
import java.text.FieldPosition

class MainActivity : AppCompatActivity(), AdapterListenerInterface{

    lateinit var mainBinding: ActivityMainBinding
    lateinit var videosViewModel: VideosViewModel
    lateinit var videoRVAdapter: VideoRVAdapter
    lateinit var simpleExoPlayer: SimpleExoPlayer
    lateinit var modelNew: VideoModelNew
    var mPosition: Int = -1
    lateinit var btFullScreen: ImageView
    var flag: Boolean = false
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        btFullScreen = findViewById(R.id.exo_fullscreen)
        mainBinding.rvVideoList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        videosViewModel = ViewModelProvider(this).get(VideosViewModel::class.java)
        setClickListeners()
        getVideoList()
        initExoPlayer()
        observeLiveData()
    }

    private fun initExoPlayer() {
        val loadControl: LoadControl = DefaultLoadControl()
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        val trackSelector: TrackSelector =
            DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl)
    }

    private fun loadVideoUrl(model: VideoModelNew,position: Int) {

        val factory = DefaultHttpDataSourceFactory("exoplayer_video")
        val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
        if (UtilityFunctions.checkIsNotNull(model.sources[0])) {
            val videoUrl = Uri.parse(model.sources[0])
            val mediaSource: MediaSource =
                ExtractorMediaSource(videoUrl,factory, extractorsFactory, null, null)

            mainBinding.pvPlayerView.player = simpleExoPlayer
            mainBinding.pvPlayerView.keepScreenOn = true
            simpleExoPlayer.prepare(mediaSource)
            simpleExoPlayer.playWhenReady = true
            setCircularProgressBar()
        }
        simpleExoPlayer.addListener(object : Player.EventListener {
            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                //TODO("Not yet implemented")
            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray?,
                trackSelections: TrackSelectionArray?
            ) {
                //TODO("Not yet implemented")
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                //TODO("Not yet implemented")
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                setCircularProgressBar()
                if (playbackState == Player.STATE_BUFFERING)
                    mainBinding.progressBar.visibility = View.VISIBLE
                else if (playbackState == Player.STATE_READY)
                    mainBinding.progressBar.visibility = View.GONE
                else if (playbackState == Player.STATE_ENDED){
                    simpleExoPlayer.seekToDefaultPosition()
                    simpleExoPlayer.playWhenReady = false
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                //TODO("Not yet implemented")
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                //TODO("Not yet implemented")
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                //TODO("Not yet implemented")
            }

            override fun onPositionDiscontinuity(reason: Int) {
                //TODO("Not yet implemented")
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                //TODO("Not yet implemented")
            }

            override fun onSeekProcessed() {
                //TODO("Not yet implemented")
            }


        })


        btFullScreen.setOnClickListener {
            if (flag) {
                btFullScreen.setImageDrawable(resources.getDrawable(com.google.android.exoplayer2.R.drawable.exo_controls_fullscreen_enter))
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                flag = false
            } else {
                btFullScreen.setImageDrawable(resources.getDrawable(com.google.android.exoplayer2.R.drawable.exo_controls_fullscreen_exit))
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                flag = true
            }
        }
    }

    private fun setCircularProgressBar(){
        val runnable = Runnable {
            videosViewModel.videoListLiveData?.value?.get(mPosition)?.progress = simpleExoPlayer.bufferedPercentage
            videoRVAdapter.notifyItemChanged(mPosition)
        }
        handler.postDelayed(runnable,500)
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer.playWhenReady = false
        simpleExoPlayer.playbackState
    }

    override fun onRestart() {
        super.onRestart()
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.playbackState
    }

    private fun observeLiveData() {
        videosViewModel.videoListLiveData?.observe(this, Observer<List<VideoModelNew>> { t ->
            if (t != null) {
                mainBinding.tvRefresh.visibility = View.GONE
                mainBinding.tvErrorText.visibility = View.VISIBLE
                mainBinding.tvErrorText.text = "Please select a video to play"
                mainBinding.rvVideoList.visibility = View.VISIBLE
                mainBinding.progressBarMain.visibility = View.GONE
                setAdapter(t)
            }
            else{
                mainBinding.tvErrorText.text = "Error connecting to server"
                mainBinding.tvRefresh.visibility = View.VISIBLE
            }
        })
    }

    private fun setAdapter(videoList: List<VideoModelNew>) {
        videoRVAdapter = VideoRVAdapter(this, videoList, this)
        mainBinding.rvVideoList.adapter = videoRVAdapter
    }

    private fun getVideoList() {
        if (UtilityFunctions.isConnectingToInternet(this)) {
            mainBinding.progressBarMain.visibility = View.VISIBLE
            videosViewModel.getVideoList()
        }
        else {
            mainBinding.tvErrorText.visibility = View.VISIBLE
            mainBinding.tvErrorText.text = "Please check your internet connection"
            mainBinding.tvRefresh.visibility = View.VISIBLE
        }
    }

    private fun setClickListeners(){
        mainBinding.tvRefresh.setOnClickListener{
            if(UtilityFunctions.isConnectingToInternet(this)){
                mainBinding.tvRefresh.visibility = View.GONE
                getVideoList()
            }
            else
                Toast.makeText(this,"No internet !!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun loadVideoPlayer(item: VideoModelNew,position: Int) {
        mainBinding.pvPlayerView.visibility = View.VISIBLE
        mainBinding.tvErrorText.visibility = View.GONE
        mainBinding.tvTitle.visibility = View.VISIBLE
        mainBinding.tvTitle.text = item.title
        mainBinding.tvSubtitle.visibility = View.VISIBLE
        mainBinding.tvSubtitle.text = item.subtitle
        mainBinding.tvDesc.visibility = View.VISIBLE
        mainBinding.tvDesc.text = item.description
        mainBinding.tvPlayedProgress.visibility = View.VISIBLE
        modelNew = item
        mPosition = position
        loadVideoUrl(modelNew,mPosition)

    }

}