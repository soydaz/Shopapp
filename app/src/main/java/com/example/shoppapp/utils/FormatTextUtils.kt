package com.example.shoppapp.utils

import android.icu.text.DecimalFormat

class FormatTextUtils {
    companion object {

        private val formatValue = DecimalFormat("#,###,###.##")

        fun getMoneyFormat(value: Double): String {
            return "$" + formatValue.format(value)
        }
    }
}