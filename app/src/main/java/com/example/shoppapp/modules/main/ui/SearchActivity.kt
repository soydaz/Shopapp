package com.example.shoppapp.modules.main.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppapp.R
import com.example.shoppapp.databinding.ActivityMainBinding
import com.example.shoppapp.modules.main.ui.components.ArticleAdapter
import com.example.shoppapp.modules.main.ui.components.FilterSelectorDialog
import com.example.shoppapp.modules.main.ui.components.RemotePresentationState
import com.example.shoppapp.modules.main.ui.components.ReposLoadStateAdapter
import com.example.shoppapp.modules.main.ui.components.asRemotePresentationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
            pagingData = mSearchViewModel.pagingDataFlow,
            uiActions = mSearchViewModel.accept
        )
    }

    private fun bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<UiModel>>,
        uiActions: (UiAction) -> Unit
    ) {
        val articleAdapter = ArticleAdapter()
        val header = ReposLoadStateAdapter { articleAdapter.retry() }
        mBinding.list.adapter = articleAdapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = ReposLoadStateAdapter { articleAdapter.retry() }
        )

        mBinding.bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )

        mBinding.bindList(
            header = header,
            repoAdapter = articleAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )

    }

    private fun ActivityMainBinding.bindSearch(
        uiState: StateFlow<UiState>,
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

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(searchBar::setText)
        }
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
        header: ReposLoadStateAdapter,
        repoAdapter: ArticleAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<UiModel>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        retryButton.setOnClickListener { repoAdapter.retry() }
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = repoAdapter.loadStateFlow
            .asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest(repoAdapter::submitData)
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) list.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            repoAdapter.loadStateFlow.collect { loadState ->
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && repoAdapter.itemCount > 0 }
                    ?: loadState.prepend

                val isListEmpty = loadState.refresh is LoadState.NotLoading && repoAdapter.itemCount == 0
                emptyList.isVisible = isListEmpty
                list.isVisible =  loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                loading.isVisible = loadState.mediator?.refresh is LoadState.Loading
                retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error && repoAdapter.itemCount == 0
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        this@SearchActivity,
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun sendTypeFilter(type: Int) {

    }

}