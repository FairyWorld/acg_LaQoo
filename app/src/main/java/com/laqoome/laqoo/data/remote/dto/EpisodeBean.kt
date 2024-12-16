package com.laqoome.laqoo.data.remote.dto

import com.laqoome.laqoo.domain.model.Episode

data class EpisodeBean(
    val name: String,
    val url: String
) {
    fun toEpisode(): Episode {
        return Episode(name = name, url = url)
    }
}