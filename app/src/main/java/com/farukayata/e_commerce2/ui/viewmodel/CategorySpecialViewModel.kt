package com.farukayata.e_commerce2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.data.repo.ProductsRepository
import com.farukayata.e_commerce2.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategorySpecialViewModel @Inject constructor(
    private val repository: ProductsRepository
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    fun loadProductsByCategory(category: String, favorites: List<Product>? = null) {
        viewModelScope.launch {
            try {
                val products = repository.getProductsByCategory(category)

                // Favori durumu güncelle
                val updatedList = products.map { product ->
                    product.copy(isFavorite = favorites?.any { it.id == product.id } == true)
                }

                _products.value = updatedList
            } catch (e: Exception) {
                _products.value = emptyList()
            }
        }
    }


    // Favori değişiklikleri için güncelleme fonksiyonu
    fun updateFavorites(favorites: List<Product>) {
        val currentList = _products.value ?: return

        val updatedList = currentList.map { product ->
            val isFav = favorites.any { it.id == product.id }
            if (product.isFavorite != isFav) {
                product.copy(isFavorite = isFav)
            } else {
                product
            }
        }
        _products.postValue(updatedList)
    }

}