package com.laqoome.laqoo.data.repository

import com.laqoo.danmaku.api.DanmakuSession
import com.laqoome.laqoo.data.remote.dandanplay.DanmakuProvider
import com.laqoome.laqoo.domain.repository.DanmakuRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DanmakuRepositoryImpl @Inject constructor(
    private val danmakuProvider: DanmakuProvider
) : DanmakuRepository {
    override suspend fun fetchDanmakuSession(
        subjectName: String,
        episodeName: String?
    ): DanmakuSession? {
        return try {
            danmakuProvider.fetch(subjectName, episodeName)
        } catch (_: Exception) {
            null
        }
    }
}