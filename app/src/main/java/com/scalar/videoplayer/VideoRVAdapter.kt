package com.scalar.videoplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.scalar.videoplayer.databinding.VideoListItemBinding
import com.squareup.picasso.Picasso

class VideoRVAdapter(
    context: Context,
    videoList: List<VideoModelNew>,
    adapterListenerInterface: AdapterListenerInterface
) :
    RecyclerView.Adapter<VideoRVAdapter.ViewHolder>() {


    var mContext = context
    private var mVideosList = videoList
    var callback = adapterListenerInterface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoRVAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VideoListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoRVAdapter.ViewHolder, position: Int) {
        holder.bind(mVideosList[position], position)
    }

    override fun getItemCount(): Int {
        return mVideosList.size
    }

    inner class ViewHolder(private val binding: VideoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoModelNew, position: Int) {
            with(binding) {
                if (UtilityFunctions.checkIsNotNull(item.title))
                    tvRVListTitle.text = item.title

                if (UtilityFunctions.checkIsNotNull(item.thumb)) {
                    //Picasso.get().load(item.thumb).into(ivRVListThumbnail)
                    Glide.with(mContext).asBitmap().load(item.thumb).into(ivRVListThumbnail)
                }
                if(UtilityFunctions.checkIsNotNull(item.progress.toString())){
                    if(item.progress != 0)
                        binding.circularProgressBar.progress = item.progress
                }

                cvImage.setOnClickListener{
                    callback.loadVideoPlayer(item,position)
                }
            }
        }

    }

}