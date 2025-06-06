package com.example.shoppapp.modules.main.ui.components

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppapp.databinding.ItemColorViewholderBinding
import androidx.core.graphics.toColorInt
import com.example.shoppapp.modules.main.domain.model.VariantColor

class CodeColorAdapter(private val mColorsList: List<VariantColor>): RecyclerView.Adapter<CodeColorAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder = ItemViewHolder.from(parent)

    override fun getItemCount(): Int = mColorsList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val variant = mColorsList[position]
        holder.bind(variant)
    }

    class ItemViewHolder(private val mBinding: ItemColorViewholderBinding) : RecyclerView.ViewHolder(mBinding.root) {

        companion object {
            fun from(parent: ViewGroup) = ItemViewHolder(
                ItemColorViewholderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        fun bind(variantColor: VariantColor) {
            if (variantColor.colorHex.isNullOrEmpty()) {
                mBinding.color.visibility = View.GONE
            } else {
                mBinding.color.backgroundTintList = ColorStateList.valueOf(variantColor.colorHex.toColorInt())
            }
            mBinding.executePendingBindings()
        }

    }

}