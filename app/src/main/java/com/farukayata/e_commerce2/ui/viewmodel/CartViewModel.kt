package com.farukayata.e_commerce2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.data.repo.CartRepository
import com.farukayata.e_commerce2.model.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CartRepository
) : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartItem>>() // Sepet ürünleri listesi
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    init {
        loadCartItems() // ViewModel başlatıldığında sepeti yükle
    }

    //Firestore'dan Sepetteki Ürünleri Getir
    fun loadCartItems() {
        viewModelScope.launch {
            _cartItems.value = repository.getCartItems()
        }
    }
    //sadece firestore dan veri çekip UI a yansıttıyor yukarıda
    //burda try catch yapmadık çünkü burda hata alırsak zaten repository.getCartItems() içinde hata yönetimi var
    //repo kısmında


    //Try-catch ekleyerek interet bağlantısı kesildiğinde veya firestore'da hara olduğunda uygulamapatlamıcak
    //hata loglancak


    fun addToCart(newCartItem: CartItem) {
        viewModelScope.launch {
            try {
                val currentCartItems = repository.getCartItems().toMutableList() // Firestore'dan güncel veriyi çekiyoruz
                val existingItemIndex = currentCartItems.indexOfFirst { it.id == newCartItem.id.orEmpty() }

                if (existingItemIndex != -1) {
                    val existingItem = currentCartItems[existingItemIndex]
                    val updatedCount = (existingItem.count ?: 0) + (newCartItem.count ?: 0)

                    val updatedItem = existingItem.copy(count = updatedCount)
                    currentCartItems[existingItemIndex] = updatedItem

                    repository.updateItemCount(newCartItem.id.orEmpty(), updatedCount) // Firestore'da güncelle
                } else {
                    currentCartItems.add(newCartItem)
                    repository.addToCart(newCartItem) // Firestore'a yeni ürün ekle
                }

                _cartItems.postValue(currentCartItems) // UI'yi güncelle
                loadCartItems() // Firestore'dan tekrar veri çek
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            try {
                repository.removeFromCart(productId)
                loadCartItems()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun updateItemCount(productId: String, newCount: Int) {
        viewModelScope.launch {
            try {
                if (newCount > 0) {
                    repository.updateItemCount(productId, newCount)
                    loadCartItems() // UI'yi güncelle
                } else {
                    removeFromCart(productId) // Eğer 0 olursa ürünü sepetten kaldır
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    //satın alınan ürünnleri sepetten kaldırcak
    fun clearCart() {
        viewModelScope.launch {
            try {
                repository.clearCart()
                _cartItems.value = emptyList() //UI'yi güncellemek için
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
