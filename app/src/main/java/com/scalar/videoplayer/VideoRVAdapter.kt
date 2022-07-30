package com.scalar.videoplayer

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scalar.videoplayer.databinding.VideoListItemBinding

class VideoRVAdapter(context:Context,videoList:List<VideoModel>,adapterListenerInterface: AdapterListenerInterface): RecyclerView.Adapter<VideoRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoRVAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: VideoRVAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class ViewHolder(private val binding: VideoListItemBinding):
    RecyclerView.ViewHolder(binding.root){

    }

}