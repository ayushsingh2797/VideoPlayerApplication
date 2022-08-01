package com.scalar.videoplayer

import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.scalar.videoplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), AdapterListenerInterface {

    lateinit var mainBinding: ActivityMainBinding
    lateinit var videosViewModel: VideosViewModel
    lateinit var videoRVAdapter: VideoRVAdapter
    lateinit var simpleExoPlayer: SimpleExoPlayer
    lateinit var modelNew: VideoModelNew
    var mPosition: Int = -1
    private lateinit var btFullScreen: ImageView
    lateinit var btMuteUnMute: ImageView
    var isFullScreen: Boolean = false
    var mute: Boolean = false
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        btFullScreen = findViewById(R.id.exo_fullscreen)
        btMuteUnMute = findViewById(R.id.exo_mute)
        mainBinding.rvVideoList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        videosViewModel = ViewModelProvider(this).get(VideosViewModel::class.java)
        setOptionsMenu()
        setClickListeners()
        getVideoList()
        initExoPlayer()
        observeLiveData()
    }

    private fun fullScreen(){
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        supportActionBar?.hide()
    }

    private fun removeFullScreen(){
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        supportActionBar?.show()
    }
    private fun initExoPlayer() {
        val loadControl: LoadControl = DefaultLoadControl()
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        val trackSelector: TrackSelector =
            DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl)
        mainBinding.pvPlayerView.player = simpleExoPlayer
        mainBinding.pvPlayerView.keepScreenOn = true
    }

    private fun loadVideoUrl(model: VideoModelNew, position: Int) {

        val factory = DefaultHttpDataSourceFactory("exoplayer_video")
        val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
        if (UtilityFunctions.checkIsNotNull(model.sources[0])) {
            val videoUrl = Uri.parse(model.sources[0])
            val mediaSource: MediaSource =
                ExtractorMediaSource(videoUrl, factory, extractorsFactory, null, null)


            simpleExoPlayer.prepare(mediaSource)
            simpleExoPlayer.playWhenReady = true
            setCircularProgressBar(position)
        }
        simpleExoPlayer.addListener(object : Player.EventListener {
            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                setCircularProgressBar(position)
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
                setCircularProgressBar(position)
                if (playbackState == Player.STATE_BUFFERING)
                    mainBinding.progressBar.visibility = View.VISIBLE
                else if (playbackState == Player.STATE_READY)
                    mainBinding.progressBar.visibility = View.GONE
                else if (playbackState == Player.STATE_ENDED) {
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
                setCircularProgressBar(position)
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                //TODO("Not yet implemented")
            }

            override fun onSeekProcessed() {
                setCircularProgressBar(position)
            }


        })


        btFullScreen.setOnClickListener {
            if (isFullScreen) {
                isFullScreen = false
                playInFullScreen(false)
            } else {
                isFullScreen = true
                playInFullScreen(true)
            }
        }

        btMuteUnMute.setOnClickListener {
            if (mute) {
                btMuteUnMute.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_volume_up
                    )
                )
                simpleExoPlayer.volume = 1f
                mute = false
            } else {
                btMuteUnMute.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_volume_off
                    )
                )
                simpleExoPlayer.volume = 0f
                mute = true
            }
            btMuteUnMute
        }
    }

    private fun setCircularProgressBar(position: Int) {
        val runnable = Runnable {
            videosViewModel.videoListLiveData?.value?.get(position)?.progress =
                simpleExoPlayer.bufferedPercentage
            videoRVAdapter.notifyItemChanged(position)
        }
        handler.postDelayed(runnable, 500)
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
                //mainBinding.tvErrorText.visibility = View.VISIBLE
                //mainBinding.tvErrorText.text = "Please select a video to play"
                mainBinding.rvVideoList.visibility = View.VISIBLE
                mainBinding.progressBarMain.visibility = View.GONE
                supportActionBar?.show()
                setAdapter(t)
            } else {
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
        } else {
            mainBinding.tvErrorText.visibility = View.VISIBLE
            mainBinding.tvErrorText.text = "Please check your internet connection"
            mainBinding.tvRefresh.visibility = View.VISIBLE
        }
    }

    private fun setClickListeners() {
        mainBinding.tvRefresh.setOnClickListener {
            if (UtilityFunctions.isConnectingToInternet(this)) {
                mainBinding.tvRefresh.visibility = View.GONE
                getVideoList()
            } else
                Toast.makeText(this, "No internet !!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun loadVideoPlayer(item: VideoModelNew, position: Int) {
        mainBinding.pvPlayerView.visibility = View.VISIBLE
        mainBinding.tvErrorText.visibility = View.GONE
        mainBinding.tvTitle.visibility = View.VISIBLE
        mainBinding.tvTitle.text = item.title
        mainBinding.tvSubtitle.visibility = View.VISIBLE
        mainBinding.tvSubtitle.text = item.subtitle
        mainBinding.tvDesc.visibility = View.VISIBLE
        mainBinding.tvDesc.text = item.description
        //mainBinding.tvPlayedProgress.visibility = View.VISIBLE
        modelNew = item
        mPosition = position
        setViewsVideos(item.title,position)
        loadVideoUrl(modelNew, mPosition)
    }

    private fun playInFullScreen(enable: Boolean) {
        if (enable) {
            mainBinding.pvPlayerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            simpleExoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            btFullScreen.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    com.google.android.exoplayer2.R.drawable.exo_controls_fullscreen_exit
                )
            )
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            fullScreen()
            hideAllViews()
        } else {
            mainBinding.pvPlayerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            simpleExoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            btFullScreen.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    com.google.android.exoplayer2.R.drawable.exo_controls_fullscreen_enter
                )
            )
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            removeFullScreen()
            showAllViews()
        }
    }

    private fun hideAllViews(){
        with(mainBinding){
            mainToolbar.visibility = View.GONE
            tvRefresh.visibility = View.GONE
            tvPlayedProgress.visibility = View.GONE
            tvErrorText.visibility = View.GONE
            tvTitle.visibility = View.GONE
            progressBarMain.visibility = View.GONE
            tvSubtitle.visibility = View.GONE
            tvDesc.visibility = View.GONE
            rvVideoList.visibility = View.GONE
        }
    }

    private fun showAllViews(){
        with(mainBinding){
            mainToolbar.visibility = View.VISIBLE
            tvTitle.visibility = View.VISIBLE
            tvSubtitle.visibility = View.VISIBLE
            tvDesc.visibility = View.VISIBLE
            rvVideoList.visibility = View.VISIBLE
        }
    }

    private fun setOptionsMenu(){
        val toolbar  = mainBinding.mainToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sort_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.mViews ->{
                sortByViews()
                true
            }
            else ->{
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setViewsVideos(title:String,position: Int){
        val currentViews = UtilityFunctions.getViewsOfVideos(this,title)
        UtilityFunctions.setViewsForVideo(this,title,currentViews+1)
        //mVideosList?.get(position)?.views = currentViews
    }

    private fun sortByViews() {
        val videoList = videosViewModel.videoListLiveData?.value as MutableList<VideoModelNew>
        for(item in videoList){
            val currentViews = UtilityFunctions.getViewsOfVideos(this,item.title)
            item.views = currentViews
        }
        videoList.sortBy { it.views }
        videosViewModel.videoListLiveData?.value = videoList
    }

    override fun onBackPressed() {
        if(isFullScreen) {
            isFullScreen = false
            playInFullScreen(false)
        }
        else
            super.onBackPressed()
    }

}