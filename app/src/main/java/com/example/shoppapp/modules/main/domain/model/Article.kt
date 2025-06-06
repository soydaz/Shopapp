package com.example.shoppapp.modules.main.domain.model

import com.google.gson.annotations.SerializedName

data class Article(
    @field:SerializedName("id") val id: Int,
    @field:SerializedName("productId") val productId: String?,
    @field:SerializedName("productDisplayName") val productDisplayName: String?,
    @field:SerializedName("listPrice") val listPrice: Double?,
    @field:SerializedName("promoPrice") val promoPrice: Double?,
    @field:SerializedName("smImage") val image: String?,
    @field:SerializedName("variantsColor") val variantsColor:List<VariantColor>? = emptyList()
)

data class VariantColor(
    @SerializedName("colorName") val colorName: String?,
    @SerializedName("colorHex") val colorHex: String?
)