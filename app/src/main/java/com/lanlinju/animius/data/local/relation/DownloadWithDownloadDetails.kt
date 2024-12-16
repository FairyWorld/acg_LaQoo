package com.laqoome.laqoo.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.laqoome.laqoo.data.local.entity.DownloadDetailEntity
import com.laqoome.laqoo.data.local.entity.DownloadEntity
import com.laqoome.laqoo.domain.model.Download
import com.laqoome.laqoo.util.SourceMode

data class DownloadWithDownloadDetails(
    @Embedded val download: DownloadEntity,
    @Relation(
        parentColumn = "download_id",
        entityColumn = "download_id",
    )
    val downloadDetails: List<DownloadDetailEntity>
) {
    fun toDownload(): Download {
        val totalSize = downloadDetails.fold(0L) { acc, d -> acc + d.fileSize }
        return Download(
            title = download.title,
            detailUrl = download.detailUrl,
            imgUrl = download.imgUrl,
            sourceMode = SourceMode.valueOf(download.source),
            totalSize = totalSize,
            downloadDetails = downloadDetails.map { it.toDownloadDetail() }
        )
    }
}
