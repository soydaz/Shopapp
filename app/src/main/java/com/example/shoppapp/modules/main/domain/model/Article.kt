package com.example.shoppapp.modules.main.domain.model

import com.google.gson.annotations.SerializedName

data class Article(
    @SerializedName("id") val id: Int,
    @SerializedName("productId") val productId: String?,
    @SerializedName("productDisplayName") val productDisplayName: String?,
    @SerializedName("listPrice") val listPrice: Double?,
    @SerializedName("promoPrice") val promoPrice: Double?,
    @SerializedName("smImage") val image: String?,
    @SerializedName("variantsColor") val variantsColor:List<VariantColor>? = emptyList()
)

data class VariantColor(
    @SerializedName("colorName") val colorName: String?,
    @SerializedName("colorHex") val colorHex: String?
)