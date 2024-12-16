package com.laqoome.laqoo.data.remote.api

import com.laqoome.laqoo.data.remote.dto.laqooBean
import com.laqoome.laqoo.data.remote.dto.laqooDetailBean
import com.laqoome.laqoo.data.remote.dto.HomeBean
import com.laqoome.laqoo.data.remote.dto.VideoBean
import com.laqoome.laqoo.util.SourceMode

interface laqooApi {
    suspend fun getHomeAllData(): List<HomeBean>

    suspend fun getlaqooDetail(detailUrl: String, mode: SourceMode): laqooDetailBean

    suspend fun getVideoData(episodeUrl: String, mode: SourceMode): VideoBean

    suspend fun getSearchData(query: String, page: Int, mode: SourceMode): List<laqooBean>

    suspend fun getWeekDate(): Map<Int, List<laqooBean>>
}