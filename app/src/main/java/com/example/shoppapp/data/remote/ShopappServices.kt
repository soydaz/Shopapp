package com.example.shoppapp.data.remote

import com.example.shoppapp.data.model.SearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ShopappServices {

    @GET("appclienteservices/services/v3/plp")
    suspend fun searchItems(@Query("search-string") query: String, @Query("page-number") page: Int): SearchResponse

    @GET("appclienteservices/services/v3/plp")
    suspend fun searchItemsByTypePrices(@Query("search-string") query: String, @Query("page-number") page: Int, @Query("minSortPrice") filter: Int): SearchResponse

    companion object {

        private const val BASE_URL = "https://shoppapp.liverpool.com.mx/"

        fun create(): ShopappServices {
            val logger = HttpLoggingInterceptor()
            logger.level = Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ShopappServices::class.java)
        }

    }
}