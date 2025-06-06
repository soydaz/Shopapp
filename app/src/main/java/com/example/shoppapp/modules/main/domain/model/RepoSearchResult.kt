package com.example.shoppapp.modules.main.domain.model

import java.lang.Exception

sealed class RepoSearchResult {
    data class Success(val data: List<Article>) : RepoSearchResult()
    data class Error(val error: Exception) : RepoSearchResult()
}