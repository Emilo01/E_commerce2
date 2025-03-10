package com.farukayata.e_commerce2.data.datasource


import com.farukayata.e_commerce2.model.Product
import com.farukayata.e_commerce2.network.ApiService
import javax.inject.Inject

class ProductsDataSource @Inject constructor(private val apiService: ApiService) {
    suspend fun getProductsFromApi(): List<Product> {
        return apiService.getProducts()
    }

    // Belirli bir kategoriye göre ürünleri getir
    suspend fun getCategoryProducts(category: String): List<Product> {
        return apiService.getCategoryProducts(category)
    }
}


