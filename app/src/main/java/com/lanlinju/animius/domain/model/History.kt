package com.laqoome.laqoo.domain.model

import com.laqoome.laqoo.data.local.entity.HistoryEntity
import com.laqoome.laqoo.util.SourceMode

data class History(
    val title: String,
    val imgUrl: String,
    val detailUrl: String,
    val lastEpisodeName: String = "",
    val lastEpisodeUrl: String = "",
    val sourceMode: SourceMode,
    val time: String = "",
    val episodes: List<Episode>
) {
    fun toHistoryEntity(): HistoryEntity {
        return HistoryEntity(
            title = title,
            imgUrl = imgUrl,
            detailUrl = detailUrl,
            source = sourceMode.name
        )
    }
}
