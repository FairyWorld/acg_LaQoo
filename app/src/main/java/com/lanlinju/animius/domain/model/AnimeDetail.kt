package com.laqoome.laqoo.domain.model


data class laqooDetail(
    val title: String,
    val img: String,
    val desc: String,
    val tags: List<String>,
    val lastPosition: Int,
    val episodes: List<Episode>,
    val relatedlaqoos: List<laqoo>,
    val channelIndex: Int = 0,
    val channels: Map<Int, List<Episode>> = emptyMap(),
)
