package com.farukayata.e_commerce2.ui.viewmodel

import android.util.Log
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

    val recommendedProducts = MutableLiveData<List<Product>>() // Önerilen ürünler
    private val favoriteProducts = MutableLiveData<List<Product>>() // Favori ürünleri saklayan liste

    fun loadRecommendedProducts(category: String, currentProductId: Int, favorites: List<Product>? = null) {
        viewModelScope.launch {
            try {
                val products = repository.getProductsByCategory(category)
                Log.d("DetailViewModel", "Firestore'dan gelen ürünler: $products")

                val filteredProducts = products.filter { it.id != currentProductId }

                //Favori durumunu güncellettik
                val updatedList = filteredProducts.map { product ->
                    product.copy(isFavorite = favorites?.any { it.id == product.id } == true)
                }

                recommendedProducts.value = updatedList
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Önerilen ürünler yüklenemedi", e)
                recommendedProducts.value = emptyList()
            }
        }
    }

    fun updateFavorites(favorites: List<Product>) {
        val currentList = recommendedProducts.value ?: return

        // favori durumu değişenleri güncelledik
        val updatedList = currentList.map { product ->
            val isFav = favorites.any { it.id == product.id }
            if (product.isFavorite != isFav) {
                product.copy(isFavorite = isFav)
            } else {
                product
            }
        }

        recommendedProducts.postValue(updatedList)
    }
}






/*
package com.farukayata.e_commerce2.ui.viewmodel

import android.util.Log
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
    private val favoriteProducts = MutableLiveData<List<Product>>() // Favori ürünleri saklayan liste
    

    fun loadRecommendedProducts(category: String, currentProductId: Int) {
        viewModelScope.launch {
            try {
                val products = repository.getProductsByCategory(category)
                Log.d("DetailViewModel", "Firestore'dan gelen ürünler: $products")
                //şu anki detay sayfasındaki ürünü benzer ürünlerden kaldır
                val filteredProducts = products.filter { it.id != currentProductId }
                recommendedProducts.value = filteredProducts
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Önerilen ürünler yüklenemedi", e)
                e.printStackTrace()
                recommendedProducts.value = emptyList() //hata olursa boş liste döndürcek
            }
        }
    }
//    fun updateFavorites1(favorites: List<Product>) {
//        favoriteProducts.value = favorites
//        recommendedProducts.value = recommendedProducts.value?.map { product ->
//            product.copy(isFavorite = favorites.any { it.id == product.id })
//        }
//    }

//    fun updateFavorites1(favorites: List<Product>) {
    //yöntem eski listeyi yanlışlıkla sıfırlayabilir
//        //doğrudan eski listeyi değiştirmek yerine, yeni bir updatedList oluşturduk.
//        val updatedList = recommendedProducts.value?.map { product ->
//            product.copy(isFavorite = favorites.any { it.id == product.id })
//        } ?: emptyList()
//
//        recommendedProducts.postValue(updatedList) // Listeyi güncelleyerek UI'ya bildir
//    }

    fun updateFavorites1(favorites: List<Product>) {
        val currentList = recommendedProducts.value ?: return

        // Sadece değişiklik olan ürünleri güncelle
        val updatedList = currentList.map { product ->
            val isFav = favorites.any { it.id == product.id }
            if (product.isFavorite != isFav) {
                product.copy(isFavorite = isFav) // Değişiklik varsa kopyala
            } else {
                product // Değişiklik yoksa aynısını kullan
            }
        }

        recommendedProducts.postValue(updatedList) // UI'yi zorla güncelle
    }



}


 */