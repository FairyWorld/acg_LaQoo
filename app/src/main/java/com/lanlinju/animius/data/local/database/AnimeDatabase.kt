package com.laqoome.laqoo.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.laqoome.laqoo.data.local.dao.DownloadDao
import com.laqoome.laqoo.data.local.dao.DownloadDetailDao
import com.laqoome.laqoo.data.local.dao.EpisodeDao
import com.laqoome.laqoo.data.local.dao.FavouriteDao
import com.laqoome.laqoo.data.local.dao.HistoryDao
import com.laqoome.laqoo.data.local.entity.DownloadDetailEntity
import com.laqoome.laqoo.data.local.entity.DownloadEntity
import com.laqoome.laqoo.data.local.entity.EpisodeEntity
import com.laqoome.laqoo.data.local.entity.FavouriteEntity
import com.laqoome.laqoo.data.local.entity.HistoryEntity

@Database(
    version = 3,
    entities = [
        FavouriteEntity::class,
        HistoryEntity::class,
        EpisodeEntity::class,
        DownloadEntity::class,
        DownloadDetailEntity::class
    ]
)
abstract class laqooDatabase : RoomDatabase() {
    abstract fun favouriteDao(): FavouriteDao
    abstract fun historyDao(): HistoryDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun downLoadDao(): DownloadDao
    abstract fun downloadDetailDao(): DownloadDetailDao
}