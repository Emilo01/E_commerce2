package com.farukayata.e_commerce2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.data.repo.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> get() = _categories

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val categoryList = repository.fetchCategories()
                // API'den kategorileri al başta burda getCategories() vardı
                _categories.value = categoryList
            } catch (e: Exception) {
                _categories.value = emptyList() // Hata durumunda boş liste döner
            }
        }
    }
}


