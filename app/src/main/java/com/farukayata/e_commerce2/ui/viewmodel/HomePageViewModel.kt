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
    val productList = MutableLiveData<List<Product>>()

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                val products = repository.productsYukle()
                productList.value = products
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}




















/*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.farukayata.e_commerce2.data.entity.Commerce_Products
import com.farukayata.e_commerce2.data.repo.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor (var prepo : ProductsRepository) : ViewModel() {


    //homepage a baplamak lazım
    //aşağıdaki hilt kullannımınna uygun değil bu yüzde üstteki kısım gibi olcak
    //var prepo = ProductsRepository()//bu bir bağımlılık
    var commerceProductListesi = MutableLiveData<List<Commerce_Products>>()

    init {
        productsYukle()
    }
    //anasayfaviewmodelden nesne oluşturduğum anda ürünleri getircek

    fun productsYukle(){
        CoroutineScope(Dispatchers.Main).launch {
            //burda ıo değil main dedik veriye yakın datasoruce ile ilgili işlem olursa io derdik
            commerceProductListesi.value = prepo.productsYukle()
            //veritabanından tüm ürünleri alıp livedatayı tetiklesin

        }
    }
}

 */

//anasayfa view modelden anasayfa fragmentta veri taşımak için live data kullanmak zorundayız