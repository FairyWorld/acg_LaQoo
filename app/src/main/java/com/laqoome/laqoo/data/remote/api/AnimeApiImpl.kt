package com.laqoome.laqoo.data.remote.api

import com.laqoome.laqoo.data.remote.dto.laqooBean
import com.laqoome.laqoo.data.remote.dto.laqooDetailBean
import com.laqoome.laqoo.data.remote.dto.HomeBean
import com.laqoome.laqoo.data.remote.dto.VideoBean
import com.laqoome.laqoo.util.SourceHolder
import com.laqoome.laqoo.util.SourceMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class laqooApiImpl @Inject constructor() : laqooApi {
    override suspend fun getHomeAllData(): List<HomeBean> {
        val laqooSource = SourceHolder.currentSource
        return laqooSource.getHomeData()
    }

    override suspend fun getlaqooDetail(detailUrl: String, mode: SourceMode): laqooDetailBean {
        val laqooSource = SourceHolder.getSource(mode)
        return laqooSource.getlaqooDetail(detailUrl)
    }

    override suspend fun getVideoData(episodeUrl: String, mode: SourceMode): VideoBean {
        val laqooSource = SourceHolder.getSource(mode)
        return laqooSource.getVideoData(episodeUrl)
    }

    override suspend fun getSearchData(query: String, page: Int, mode: SourceMode): List<laqooBean> {
        val laqooSource = SourceHolder.getSource(mode)
        return laqooSource.getSearchData(query, page)
    }

    override suspend fun getWeekDate(): Map<Int, List<laqooBean>> {
        val laqooSource = SourceHolder.currentSource
        return laqooSource.getWeekData()
    }
}