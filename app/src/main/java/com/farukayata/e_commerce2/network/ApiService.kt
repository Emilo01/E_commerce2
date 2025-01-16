package com.farukayata.e_commerce2.network

import com.farukayata.e_commerce2.model.Product
import retrofit2.http.GET

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>

}