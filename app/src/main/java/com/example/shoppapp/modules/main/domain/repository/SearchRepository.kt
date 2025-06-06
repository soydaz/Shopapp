package com.example.shoppapp.modules.main.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shoppapp.data.remote.ShopappServices
import com.example.shoppapp.modules.main.domain.datasource.SearchPagingSource
import com.example.shoppapp.modules.main.domain.model.Article
import kotlinx.coroutines.flow.Flow

class SearchRepository(private val service: ShopappServices) {

    companion object {
        const val NETWORK_PAGE_SIZE = 56
    }

    fun getSearchResultStream(query: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchPagingSource(service, query) }
        ).flow
    }

}