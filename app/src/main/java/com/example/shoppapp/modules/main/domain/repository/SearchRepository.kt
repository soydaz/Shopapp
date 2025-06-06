package com.example.shoppapp.modules.main.domain.repository

import com.example.shoppapp.data.remote.ShopappServices
import com.example.shoppapp.modules.main.domain.model.Article
import com.example.shoppapp.modules.main.domain.model.RepoSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import retrofit2.HttpException
import java.io.IOException


private const val SHOPAPP_STARTING_PAGE_INDEX = 1

class SearchRepository(private val service: ShopappServices) {

    private val inMemoryCache = mutableListOf<Article>()

    private val searchResults = MutableSharedFlow<RepoSearchResult>(replay = 1)

    private var lastRequestedPage = SHOPAPP_STARTING_PAGE_INDEX

    private var isRequestInProgress = false

    companion object {
        const val NETWORK_PAGE_SIZE = 56
    }

    suspend fun getSearchResultStream(query: String): Flow<RepoSearchResult> {
        lastRequestedPage = 1
        inMemoryCache.clear()
        requestAndSaveData(query)

        return searchResults
    }

    suspend fun requestMore(query: String) {
        if (isRequestInProgress) return
        val successful = requestAndSaveData(query)
        if (successful) {
            lastRequestedPage++
        }
    }


    private suspend fun requestAndSaveData(query: String): Boolean {
        isRequestInProgress = true
        var successful = false

        val apiQuery = query
        try {
            val response = service.searchItems(apiQuery, lastRequestedPage)
            val repos = response.searchResult?.records ?: emptyList()
            inMemoryCache.addAll(repos)
            searchResults.emit(RepoSearchResult.Success(repos))
            successful = true
        } catch (exception: IOException) {
            searchResults.emit(RepoSearchResult.Error(exception))
        } catch (exception: HttpException) {
            searchResults.emit(RepoSearchResult.Error(exception))
        }
        isRequestInProgress = false
        return successful
    }


}