package com.farukayata.e_commerce2.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.data.repo.ProductsRepository
import com.farukayata.e_commerce2.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: ProductsRepository) : ViewModel() {

    val recommendedProducts = MutableLiveData<List<Product>>() //önerilen ürünler listesi

    fun loadRecommendedProducts(category: String, currentProductId: Int) {
        viewModelScope.launch {
            try {
                val products = repository.getProductsByCategory(category)
                //şu anki detay sayfasındaki ürünü benzer ürünlerden kaldır
                val filteredProducts = products.filter { it.id != currentProductId }
                recommendedProducts.value = filteredProducts
            } catch (e: Exception) {
                e.printStackTrace()
                recommendedProducts.value = emptyList() //hata olursa boş liste döndürcek
            }
        }
    }

}
