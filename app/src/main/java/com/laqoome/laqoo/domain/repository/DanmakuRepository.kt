package com.laqoome.laqoo.domain.repository

import com.laqoo.danmaku.api.DanmakuSession

interface DanmakuRepository {
    suspend fun fetchDanmakuSession(subjectName: String, episodeName: String?): DanmakuSession?
}