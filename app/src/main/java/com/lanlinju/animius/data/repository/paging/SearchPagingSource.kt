package com.laqoome.laqoo.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.laqoome.laqoo.data.remote.api.laqooApi
import com.laqoome.laqoo.domain.model.laqoo
import com.laqoome.laqoo.util.SourceMode

class SearchPagingSource(
    private val api: laqooApi,
    private val query: String,
    private val mode: SourceMode,
) : PagingSource<Int, laqoo>() {
    override fun getRefreshKey(state: PagingState<Int, laqoo>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, laqoo> {
        val currentPage = params.key ?: 1

        return try {
            val response = api.getSearchData(query, currentPage, mode)

            val endOfPaginationReached = response.isEmpty()

            LoadResult.Page(
                data = response.map { it.tolaqoo() },
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (endOfPaginationReached) null else currentPage + 1
            )
        } catch (exp: Exception) {
            LoadResult.Error(exp)
        }

    }
}