package com.example.shoppapp.modules.main.ui.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.shoppapp.R
import com.example.shoppapp.databinding.LayoutFilterSelectionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterSelectorDialog(private val listener: OnItemClickListener) : BottomSheetDialogFragment() {

    lateinit var mBinding: LayoutFilterSelectionBinding

    companion object {
        @JvmStatic
        fun newInstance(listener: OnItemClickListener): FilterSelectorDialog = FilterSelectorDialog(listener)
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.layout_filter_selection, null, false)

        mBinding.actionFilterDefault.setOnClickListener {
            listener.sendTypeFilter(-1)
            dismiss()
        }

        mBinding.actionFilterHigherPrice.setOnClickListener {
            listener.sendTypeFilter(1)
            dismiss()
        }

        mBinding.actionFilterLowPrice.setOnClickListener {
            listener.sendTypeFilter(0)
            dismiss()
        }

        return mBinding.root
    }

    interface OnItemClickListener {
        fun sendTypeFilter(type: Int)
    }
}