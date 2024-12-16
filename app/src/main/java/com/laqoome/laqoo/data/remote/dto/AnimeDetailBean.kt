package com.laqoome.laqoo.data.remote.dto

import com.laqoome.laqoo.domain.model.laqooDetail

data class laqooDetailBean(
    val title: String,
    val imgUrl: String,
    val desc: String,
    val tags: List<String> = emptyList(),
    val relatedlaqoos: List<laqooBean>,
    val episodes: List<EpisodeBean> = emptyList(),  /* 保持对旧的数据兼容, 如果支持多线路则需要置为空 */
    val channels: Map<Int, List<EpisodeBean>> = emptyMap(), /* 剧集多线路支持 */
) {
    fun tolaqooDetail(): laqooDetail {
        val tempChannels = if (episodes.isNotEmpty()) { /* 保持对旧的不支持多线路的兼容 */
            mapOf(0 to episodes.map { it.toEpisode() })
        } else {
            channels.mapValues { it.value.map { it.toEpisode() } }
        }
        return laqooDetail(
            title = title,
            img = imgUrl,
            desc = desc,
            tags = tags.map { it.uppercase() },
            lastPosition = 0,
            episodes = tempChannels[0] ?: emptyList(),
            relatedlaqoos = relatedlaqoos.map { it.tolaqoo() },
            channels = tempChannels,
        )
    }
}