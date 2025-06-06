package com.example.shoppapp.modules.main.ui.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppapp.R
import com.example.shoppapp.databinding.SearchLoadStateFooterViewItemBinding

class SearchLoadStateViewHolder(private val mBinding: SearchLoadStateFooterViewItemBinding, retry: () -> Unit) : RecyclerView.ViewHolder(mBinding.root){

    init {
        mBinding.retryButton.setOnClickListener {
            retry.invoke()
        }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            mBinding.errorMsg.text = loadState.error.localizedMessage
        }
        mBinding.progressBar.isVisible = loadState is LoadState.Loading
        mBinding.retryButton.isVisible = loadState is LoadState.Error
        mBinding.errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): SearchLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.search_load_state_footer_view_item, parent, false)
            val binding = SearchLoadStateFooterViewItemBinding.bind(view)
            return SearchLoadStateViewHolder(binding, retry)
        }
    }

}