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

    fun loadProductsByCategory(category: String) {
        viewModelScope.launch {
            try {
                _products.value = repository.getProductsByCategory(category)
            } catch (e: Exception) {
                _products.value = emptyList()
            }
        }
    }
}






/*
package com.farukayata.e_commerce2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.data.repo.CategoryRepository
import com.farukayata.e_commerce2.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategorySpecialViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _categoryProducts = MutableLiveData<List<Product>>()
    val categoryProducts: LiveData<List<Product>> get() = _categoryProducts

    fun loadCategoryProducts(category: String) {
        viewModelScope.launch {
            try {
                val products = repository.getCategoryProducts(category)
                _categoryProducts.value = products
            } catch (e: Exception) {
                _categoryProducts.value = emptyList()
            }
        }
    }
}


 */