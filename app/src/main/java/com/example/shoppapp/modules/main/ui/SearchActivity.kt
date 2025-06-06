package com.example.shoppapp.modules.main.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.shoppapp.R
import com.example.shoppapp.databinding.ActivityMainBinding
import com.example.shoppapp.modules.main.domain.model.RepoSearchResult
import com.example.shoppapp.modules.main.ui.components.ArticleAdapter
import com.example.shoppapp.modules.main.ui.components.FilterSelectorDialog

class SearchActivity : AppCompatActivity(), FilterSelectorDialog.OnItemClickListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mSearchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mSearchViewModel = ViewModelProvider(this, SearchViewModelFactory(this))[SearchViewModel::class.java]

        mBinding.lifecycleOwner = this

        mBinding.actionOpenFilter.setOnClickListener {
            FilterSelectorDialog(this).show(supportFragmentManager, "FilterSelectorDialog")
        }

        bindState(
            uiState = mSearchViewModel.state,
            uiActions = mSearchViewModel.accept
        )
    }

    private fun bindState(
        uiState: LiveData<UiState>,
        uiActions: (UiAction) -> Unit
    ) {
        val articleAdapter = ArticleAdapter()
        mBinding.bindAdapter(articleAdapter)

        mBinding.bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )

        mBinding.bindList(
            repoAdapter = articleAdapter,
            uiState = uiState,
            onScrollChanged = uiActions
        )

    }

    private fun ActivityMainBinding.bindSearch(
        uiState: LiveData<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchBar.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        uiState
            .map(UiState::query)
            .distinctUntilChanged()
            .observe(this@SearchActivity, searchBar::setText)
    }

    private fun ActivityMainBinding.updateRepoListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchBar.text.trim().let {
            if (it.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun ActivityMainBinding.bindList(
        repoAdapter: ArticleAdapter,
        uiState: LiveData<UiState>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        setupScrollListener(onScrollChanged)

        uiState
            .map(UiState::searchResult)
            .distinctUntilChanged()
            .observe(this@SearchActivity) { result ->
                when (result) {
                    is RepoSearchResult.Success -> {
                        showEmptyList(result.data.isEmpty())
                        repoAdapter.submitList(result.data)
                    }
                    is RepoSearchResult.Error -> {
                        Toast.makeText(
                            this@SearchActivity,
                            "\uD83D\uDE28 Wooops $result.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }

    private fun ActivityMainBinding.showEmptyList(show: Boolean) {
        emptyList.isVisible = show
        list.isVisible = !show
    }

    private fun ActivityMainBinding.setupScrollListener(
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        val layoutManager = list.layoutManager as LinearLayoutManager
        list.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                onScrollChanged(
                    UiAction.Scroll(
                        visibleItemCount = visibleItemCount,
                        lastVisibleItemPosition = lastVisibleItem,
                        totalItemCount = totalItemCount
                    )
                )
            }
        })
    }

    override fun sendTypeFilter(type: Int) {

    }

}

private fun ActivityMainBinding.bindAdapter(articleAdapter: ArticleAdapter) {
    list.adapter = articleAdapter
    list.layoutManager = LinearLayoutManager(list.context)
}