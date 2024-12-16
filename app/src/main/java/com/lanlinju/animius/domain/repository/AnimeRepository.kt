package com.laqoome.laqoo.domain.repository

import androidx.paging.PagingData
import com.laqoome.laqoo.domain.model.laqoo
import com.laqoome.laqoo.domain.model.laqooDetail
import com.laqoome.laqoo.domain.model.Home
import com.laqoome.laqoo.domain.model.WebVideo
import com.laqoome.laqoo.util.Resource
import com.laqoome.laqoo.util.Result
import com.laqoome.laqoo.util.SourceMode
import kotlinx.coroutines.flow.Flow

interface laqooRepository {
    suspend fun getHomeData(): Resource<List<Home>>

    suspend fun getlaqooDetail(detailUrl: String, mode: SourceMode): Resource<laqooDetail?>

    suspend fun getVideoData(episodeUrl: String, mode: SourceMode): Result<WebVideo>

    suspend fun getSearchData(query: String, mode: SourceMode): Flow<PagingData<laqoo>>

    suspend fun getWeekData(): Resource<Map<Int, List<laqoo>>>
}