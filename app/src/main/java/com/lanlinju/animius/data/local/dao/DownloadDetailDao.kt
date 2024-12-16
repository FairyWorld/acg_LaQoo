package com.lanlinju.animius.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lanlinju.animius.data.local.entity.DownloadDetailEntity
import com.lanlinju.animius.util.DOWNLOAD_DETAIL_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDetailDao {
    @Query("SELECT * FROM $DOWNLOAD_DETAIL_TABLE WHERE download_id=:downloadId ORDER BY drama_number ASC")
    fun getDownloadDetails(downloadId: Long): Flow<List<DownloadDetailEntity>>

    @Query("SELECT * FROM $DOWNLOAD_DETAIL_TABLE WHERE download_url=:downloadUrl")
    fun getDownloadDetail(downloadUrl: String): Flow<DownloadDetailEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDownloadDetail(downloadDetailEntity: DownloadDetailEntity)

    @Query("DELETE FROM $DOWNLOAD_DETAIL_TABLE WHERE download_url=:downloadUrl")
    suspend fun deleteDownloadDetail(downloadUrl: String)

    @Update
    suspend fun updateDownloadDetail(downloadDetailEntity: DownloadDetailEntity)
}