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

    // **2. Ürünü Sepete Ekle**
//    fun addToCart(cartItem: CartItem) {
//        viewModelScope.launch {
//            repository.addToCart(cartItem)
//            loadCartItems() // Sepet güncellendiğinde yeniden yükle
//        }
//    }

    //Try-catch ekleyerek interet bağlantısı kesildiğinde veya firestore'da hara olduğunda uygulamapatlamıcak
    //hata loglancak

    fun addToCart(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                repository.addToCart(cartItem)
                loadCartItems()
            } catch (e: Exception) {
                e.printStackTrace() // Hata loglama
            }
        }
    }

    // **3. Sepetten Ürünü Sil**
//    fun removeFromCart(productId: String) {
//        viewModelScope.launch {
//            repository.removeFromCart(productId)
//            loadCartItems() // Sepet güncellendiğinde yeniden yükle
//        }
//    }



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


    // **4. Ürün Adetini Güncelle (Artır / Azalt)**
//    fun updateItemCount(productId: String, newCount: Int) {
//        viewModelScope.launch {
//            repository.updateItemCount(productId, newCount)
//            loadCartItems() // Sepet güncellendiğinde yeniden yükle
//        }
//    }

    fun updateItemCount(productId: String, newCount: Int) {
        viewModelScope.launch {
            try {
                repository.updateItemCount(productId, newCount)
                loadCartItems()
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
