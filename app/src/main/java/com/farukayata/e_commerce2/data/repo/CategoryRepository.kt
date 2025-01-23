package com.farukayata.e_commerce2.data.repo

import com.farukayata.e_commerce2.data.datasource.CategoryDataSource
import com.farukayata.e_commerce2.model.Product
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val dataSource: CategoryDataSource) {
    suspend fun fetchCategories(): List<String> {
        return dataSource.getCategories()
    }

    suspend fun getCategoryProducts(category: String): List<Product> {
        return dataSource.getCategoryProducts(category)
    }
}
