package com.farukayata.e_commerce2.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.data.repo.FavoritesRepository
import com.farukayata.e_commerce2.model.Favorite
import com.farukayata.e_commerce2.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                _favorites.value = repository.getFavorites()
            } catch (e: Exception) {
                _favorites.value = emptyList() // Hata durumunda boş liste döncek
            }
        }
    }

    // Yeni bir favori ekle
    fun addFavorite(favorite: Product) {
        viewModelScope.launch {
            try {
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
                //hatalı işlemlerde buraya loglama yapılabilir
            }
        }
    }
}







/*
package com.farukayata.e_commerce2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.data.repo.FavoritesRepository
import com.farukayata.e_commerce2.model.Favorite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FavoritesRepository
) : ViewModel() {

    private val _favoritesState = MutableStateFlow<List<Favorite>>(emptyList())
    val favoritesState: StateFlow<List<Favorite>> = _favoritesState

    fun loadFavorites() {
        viewModelScope.launch {
            _favoritesState.value = repository.getFavorites()
        }
    }

    fun addFavorite(favorite: Favorite) {
        viewModelScope.launch {
            repository.addFavorite(favorite)
            loadFavorites()
        }
    }

    fun removeFavorite(productId: String) {
        viewModelScope.launch {
            repository.removeFavorite(productId)
            loadFavorites()
        }
    }
}


 */