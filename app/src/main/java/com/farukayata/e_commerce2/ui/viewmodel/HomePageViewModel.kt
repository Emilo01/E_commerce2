package com.farukayata.e_commerce2.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.farukayata.e_commerce2.data.entity.Commerce_Products
import com.farukayata.e_commerce2.data.repo.ProductsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomePageViewModel : ViewModel() {
    //homepage a baplamak lazım
    var prepo = ProductsRepository()
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

//anasayfa view modelden anasayfa fragmentta veri taşımak için live data kullanmak zorundayız