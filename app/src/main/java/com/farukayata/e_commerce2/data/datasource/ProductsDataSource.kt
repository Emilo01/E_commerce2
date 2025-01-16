package com.farukayata.e_commerce2.data.datasource

import com.farukayata.e_commerce2.data.entity.Commerce_Products
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//viewmodel kısımlarına burdan başladım
//şu anda bi nesne olmadığı için bağımlılık yok
class ProductsDataSource {
    suspend fun productsYukle(): List<Commerce_Products> =
        withContext(Dispatchers.IO) {
            //IO veri tabanına çalıştığımız için
            val commerceProductListesi = ArrayList<Commerce_Products>()
            val f1 = Commerce_Products(1, "batman", "batman", 100.0)
            val f2 = Commerce_Products(1, "ironman", "ironman", 50.0)
            val f3 = Commerce_Products(1, "kaptanamerica", "kaptanamerica", 75.0)
            val f4 = Commerce_Products(1, "superman", "superman", 600.0)
            val f5 = Commerce_Products(1, "thor", "thor", 110.0)
            val f6 = Commerce_Products(1, "wolverine", "wolverine", 999.0)
            commerceProductListesi.add(f1)
            commerceProductListesi.add(f2)
            commerceProductListesi.add(f3)
            commerceProductListesi.add(f4)
            commerceProductListesi.add(f5)
            commerceProductListesi.add(f6)

            return@withContext commerceProductListesi
        }
}