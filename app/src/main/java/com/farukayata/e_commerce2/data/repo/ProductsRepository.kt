package com.farukayata.e_commerce2.data.repo

import com.farukayata.e_commerce2.data.datasource.ProductsDataSource
import com.farukayata.e_commerce2.data.entity.Commerce_Products

class ProductsRepository {
    var pds = ProductsDataSource()
    //datasource de değişken oluşturup aşağıda fonksiyonuna erişiyoruz

    suspend fun productsYukle(): List<Commerce_Products> = pds.productsYukle()
}