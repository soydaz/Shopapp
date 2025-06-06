package com.example.shoppapp.modules.main.ui.components

import android.graphics.Paint
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.shoppapp.R
import com.example.shoppapp.databinding.ArticleViewholderBinding
import com.example.shoppapp.modules.main.domain.model.Article

class ArticleViewHolder(private val mBinding: ArticleViewholderBinding) : RecyclerView.ViewHolder(mBinding.root) {

    fun bind(product: Article) {
        mBinding.apply {
            mBinding.title.text = product.productDisplayName
            mBinding.price.text = product.listPrice.toString()
            mBinding.price.paintFlags = mBinding.price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            mBinding.discountedPrice.text = product.promoPrice.toString()

            product.image?.let { url ->
                Glide.with(mBinding.imageItem.context)
                    .load(url)
                    .error(R.drawable.icon_sell)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerInside()
                    .into(mBinding.imageItem)
            }

            product.variantsColor?.let { list ->
                val layoutManager = LinearLayoutManager(mBinding.listColors.context, LinearLayoutManager.HORIZONTAL, false)
                mBinding.listColors.layoutManager = layoutManager

                val codeColorAdapter = CodeColorAdapter(list)
                mBinding.listColors.adapter = codeColorAdapter
            }
        }
    }

}