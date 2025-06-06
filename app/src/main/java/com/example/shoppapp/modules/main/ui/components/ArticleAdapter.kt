package com.example.shoppapp.modules.main.ui.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.shoppapp.R
import com.example.shoppapp.databinding.ArticleViewholderBinding
import com.example.shoppapp.modules.main.ui.UiModel

class ArticleAdapter : PagingDataAdapter<UiModel, ArticleViewHolder>(ARTICLE_DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel!!.let {
            when (uiModel) {
                is UiModel.RepoItem -> holder.bind(uiModel.repo)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.RepoItem -> R.layout.article_viewholder
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ArticleViewholderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    companion object {
        private val ARTICLE_DIFF_CALLBACK = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return (oldItem is UiModel.RepoItem && newItem is UiModel.RepoItem &&
                        oldItem.repo.productId == newItem.repo.productId)
            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return oldItem == newItem
            }

        }
    }

}