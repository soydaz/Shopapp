package com.example.shoppapp.data.model

import com.example.shoppapp.modules.main.domain.model.Article
import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("plpResults") val searchResult: Records?
)

data class Records(
    @SerializedName("records") val records: List<Article>? = emptyList()
)
