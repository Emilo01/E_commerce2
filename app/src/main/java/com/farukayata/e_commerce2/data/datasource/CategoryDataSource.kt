package com.farukayata.e_commerce2.data.datasource

import com.farukayata.e_commerce2.model.Product
import com.farukayata.e_commerce2.network.ApiService
import javax.inject.Inject

class CategoryDataSource @Inject constructor(private val apiService: ApiService) {
    suspend fun getCategories(): List<String> {
        return apiService.getCategories()
    }

    suspend fun getCategoryProducts(category: String): List<Product> {
        return apiService.getCategoryProducts(category)
    }
}
