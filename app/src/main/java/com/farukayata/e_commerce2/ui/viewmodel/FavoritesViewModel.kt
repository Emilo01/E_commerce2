package com.farukayata.e_commerce2.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.data.repo.FavoritesRepository
import com.farukayata.e_commerce2.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//Kullanıcının favorilere eklediği ürünleri Firestore'dan almak, eklemek ve kaldırmak
//Favorilerle ilgili durumları StateFlow ile yönetmek

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FavoritesRepository
) : ViewModel() {

    // Favori ürünlerin durumunu tutan Flow
    private val _favorites = MutableStateFlow<List<Product>>(emptyList())
    val favorites: StateFlow<List<Product>> = _favorites

    // Favori ürünleri Firestore'dan yüklettik
    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val favList = repository.getFavorites()
                _favorites.value = repository.getFavorites()
                Log.d("FavoritesViewModel", "Favoriler Yüklendi: $favList")
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error loading favorites", e)
                _favorites.value = emptyList() // Hata durumunda boş liste döncek
            }
        }
    }

    // Yeni bir favori ekle
    fun addFavorite(favorite: Product) {
        viewModelScope.launch {
            try {
                favorite.isFavorite = true // Favori durumu TRUE olarak güncelleniyor
                repository.addFavorite(favorite)
                loadFavorites() // Favoriler güncellendiğinde yeniden yükle
                Log.d("FavoritesViewModel", "Favorite added: ${favorite.title}")
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error adding favorite", e)
                e.printStackTrace()
            }
        }
    }

    // Favorilerden bir ürün kaldır
    fun removeFavorite(productId: String) {
        viewModelScope.launch {
            try {
                repository.removeFavorite(productId)
                loadFavorites() // Favoriler güncellendiğinde yeniden yükle
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error removing favorite with ID: $productId", e)
                //hatalı işlemlerde buraya loglama yapılabilir
            }
        }
    }

    // Ürünün favori durumunu değiştirme (Yeni eklenen fonksiyon)
    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            try {
                if (product.isFavorite) {
                    // Eğer favoriyse, kaldır
                    repository.removeFavorite(product.id.toString())
                    product.isFavorite = false
                } else {
                    // Eğer favoride değilse, ekle
                    repository.addFavorite(product)
                    product.isFavorite = true
                }
                loadFavorites() // Favori listesi güncellendiğinde tekrar yükle
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error toggling favorite status", e)
            }
        }
    }
}


