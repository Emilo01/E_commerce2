package com.farukayata.e_commerce2.network

import com.farukayata.e_commerce2.model.Product
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("products/categories")
    suspend fun getCategories(): List<String>

    @GET("products/category/{category}")
    suspend fun getCategoryProducts(@Path("category") category: String): List<Product>

}