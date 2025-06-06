package com.example.shoppapp.modules.main.ui

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.shoppapp.data.remote.ShopappServices
import com.example.shoppapp.modules.main.domain.repository.SearchRepository

@Suppress("UNCHECKED_CAST")
class SearchViewModelFactory(owner: SavedStateRegistryOwner) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(SearchRepository(ShopappServices.create()), handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}