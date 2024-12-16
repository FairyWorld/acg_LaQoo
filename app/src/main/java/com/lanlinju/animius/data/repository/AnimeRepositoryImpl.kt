package com.laqoome.laqoo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.laqoome.laqoo.data.remote.api.laqooApi
import com.laqoome.laqoo.data.repository.paging.SearchPagingSource
import com.laqoome.laqoo.domain.model.laqoo
import com.laqoome.laqoo.domain.model.laqooDetail
import com.laqoome.laqoo.domain.model.Home
import com.laqoome.laqoo.domain.model.WebVideo
import com.laqoome.laqoo.domain.repository.laqooRepository
import com.laqoome.laqoo.util.Resource
import com.laqoome.laqoo.util.Result
import com.laqoome.laqoo.util.SEARCH_PAGE_SIZE
import com.laqoome.laqoo.util.SourceMode
import com.laqoome.laqoo.util.invokeApi
import com.laqoome.laqoo.util.map
import com.laqoome.laqoo.util.safeCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class laqooRepositoryImpl @Inject constructor(
    private val laqooApi: laqooApi
) : laqooRepository {
    override suspend fun getHomeData(): Resource<List<Home>> {
        val response = invokeApi {
            laqooApi.getHomeAllData()
        }
        return when (response) {
            is Resource.Error -> Resource.Error(error = response.error)
            is Resource.Loading -> Resource.Loading
            is Resource.Success -> Resource.Success(
                data = response.data?.map { it.toHome() }.orEmpty()
            )
        }
    }

    override suspend fun getlaqooDetail(
        detailUrl: String,
        mode: SourceMode
    ): Resource<laqooDetail?> {
        val response = invokeApi {
            laqooApi.getlaqooDetail(detailUrl, mode)
        }
        return when (val response = response) {
            is Resource.Error -> Resource.Error(error = response.error)
            is Resource.Loading -> Resource.Loading
            is Resource.Success -> Resource.Success(
                data = response.data?.tolaqooDetail()
            )
        }
    }

    override suspend fun getVideoData(episodeUrl: String, mode: SourceMode): Result<WebVideo> {
        return safeCall {
            laqooApi.getVideoData(episodeUrl, mode)
        }.map { it.toWebVideo() }
    }

    override suspend fun getSearchData(query: String, mode: SourceMode): Flow<PagingData<laqoo>> {
        return Pager(
            config = PagingConfig(pageSize = SEARCH_PAGE_SIZE),
            pagingSourceFactory = { SearchPagingSource(api = laqooApi, query, mode) }
        ).flow
    }

    override suspend fun getWeekData(): Resource<Map<Int, List<laqoo>>> {
        val response = invokeApi {
            laqooApi.getWeekDate()
        }
        return when (response) {
            is Resource.Error -> Resource.Error(error = response.error)
            is Resource.Loading -> Resource.Loading
            is Resource.Success -> Resource.Success(
                data = response.data?.mapValues { (_, v) -> v.map { it.tolaqoo() } } ?: emptyMap()
            )
        }
    }
}