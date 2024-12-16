package com.laqoome.laqoo.domain.model

import com.laqoome.laqoo.data.local.entity.FavouriteEntity
import com.laqoome.laqoo.util.SourceMode

data class Favourite(
    val title: String,
    val detailUrl: String,
    val imgUrl: String,
    val sourceMode: SourceMode
) {
    fun toFavouriteEntity(): FavouriteEntity {
        return FavouriteEntity(
            title = title,
            detailUrl = detailUrl,
            imgUrl = imgUrl,
            source = sourceMode.name
        )
    }
}
