package com.example.shoppapp.modules.main.domain.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shoppapp.data.remote.ShopappServices
import com.example.shoppapp.modules.main.domain.model.Article
import com.example.shoppapp.modules.main.domain.repository.SearchRepository.Companion.NETWORK_PAGE_SIZE
import okio.IOException
import retrofit2.HttpException

private const val SHOPAPP_STARTING_PAGE_INDEX = 1

class SearchPagingSource(private val service: ShopappServices, private val query:String) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val position = params.key ?: SHOPAPP_STARTING_PAGE_INDEX
        val apiQuery = query
        return try {
            val response = service.searchItems(apiQuery, position)
            val repos = response.searchResult?.records ?: listOf()
            val nextKey = if (repos.isEmpty()) {
                null
            } else {
                position + (params.loadSize / NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = repos,
                prevKey = if (position == SHOPAPP_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception : IOException) {
            return LoadResult.Error(exception)
        } catch (exception : HttpException) {
            return LoadResult.Error(exception)
        }
    }


}