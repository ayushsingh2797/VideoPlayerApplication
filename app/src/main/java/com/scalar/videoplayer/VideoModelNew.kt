package com.scalar.videoplayer

data class VideoModelNew(
    val description: String,
    val sources: List<String>,
    val subtitle: String,
    val thumb: String,
    val title: String,
    var views: Int,
    var progress: Int = 0
)