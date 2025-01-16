package com.farukayata.e_commerce2.data.repo

import com.farukayata.e_commerce2.data.datasource.ProductsDataSource
import com.farukayata.e_commerce2.data.entity.Commerce_Products

class ProductsRepository(var pds : ProductsDataSource) {
    //hilt içi burdan viewmodel sayfasına gittik

    //pds bağımlılık olduğu içi aşağıdaki gibi değil yukarıdaki gibi kullacaz
    //var pds = ProductsDataSource()//bu bir bağımlılık
    //datasource de değişken oluşturup aşağıda fonksiyonuna erişiyoruz

    suspend fun productsYukle(): List<Commerce_Products> = pds.productsYukle()
}