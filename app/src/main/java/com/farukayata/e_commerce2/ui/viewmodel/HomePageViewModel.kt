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
class HomePageViewModel @Inject constructor(private val repository: ProductsRepository) : ViewModel() {
    //val productList = MutableLiveData<List<Product>>()

    private val allProducts = MutableLiveData<List<Product>>() //  Tüm ürünleri tutar
    val filteredProductList = MutableLiveData<List<Product>>() //  Filtrelenen ürünleri tutar
    val mostInterestedProducts =  MutableLiveData<List<Product>>()

    private val favoriteProducts = MutableLiveData<List<Product>>() // Favori ürünleri saklayan liste


    //Mutable HomeFragment'teki ürün listesinin otomatik olarak güncellenmesini sağlar.

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                val products = repository.productsYukle()
                //productList.value = products
                allProducts.value = products // Tüm ürünleri kaydet
                filteredProductList.value = products //  Başlangıçta tüm ürünleri göster
                val sortedByRating = products.filter { it.rating?.rate != null }
                    .sortedByDescending { it.rating?.rate }
                    .take(5)
                mostInterestedProducts.value = sortedByRating

                updateFavorites(favoriteProducts.value.orEmpty()) // Favorilerle senkronize ediyor
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //firestore'dan Gelen Favorileri Güncelledik
    fun updateFavorites(favorites: List<Product>) {
        favoriteProducts.value = favorites
        filteredProductList.value = allProducts.value?.map { product ->
            product.copy(isFavorite = favorites.any { it.id == product.id })
        }
    }

    fun updateMostInterestedFavorites(favorites: List<Product>) {
        mostInterestedProducts.value = mostInterestedProducts.value?.map { product ->
            product.copy(isFavorite = favorites.any { it.id == product.id })
        }
    }


    //Ürünleri Arama ve Filtreleme İşlemi
    fun filterProducts(query: String) {
        val filteredList = allProducts.value?.filter { product ->
            product.title?.contains(query, ignoreCase = true) ?: false // arama soucu nnull sa hatas aldırmaz
        //  Küçük/büyük harf duyarlılığı yok -- null checl
        } ?: allProducts.value.orEmpty()// arama kısımı boşsa eski listeyi göstertmek için
        filteredProductList.value = filteredList // Güncellenmiş listeyi UI'ya gönder
    }
}


