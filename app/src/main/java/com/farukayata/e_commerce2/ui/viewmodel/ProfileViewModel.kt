package com.farukayata.e_commerce2.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.data.repo.UserRepository
import com.farukayata.e_commerce2.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    init {
        loadUserProfile() // ViewModel başlatıldığında kullanıcı profilini çek
    }

    //Kullanıcı Profilini Firestoredan Aldık
    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val profile = repository.getUserProfile()
                profile?.let { _userProfile.value = it }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //Kullanıcı Bilgilerini Güncelledik
    fun updateUserProfile(updatedData: UserProfile) {
        viewModelScope.launch {
            try {
                repository.updateUserProfile(updatedData)
                println("Kullanıcı Profili Güncellendi: $updatedData") // Debug
                loadUserProfile() // Güncellendikten sonra verileri tekrar çekiyor
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //Kullanıcı Çıkış Yap
    fun logout() {
        repository.logoutUser()
        _userProfile.value = UserProfile()
    // Çıkış yaptığında UI’ı temizlicek null yazınca hata veriyordu
    }

    //Kullanıcı Fotoğrafını Firebase Storage a Yükle
    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            try {
                val imageUrl = repository.uploadProfileImage(imageUri)
                if (imageUrl != null) {
                    repository.updateProfileImage(imageUrl)
                    //Firestore'daki URL'yi güncelle
                    loadUserProfile() //UI’yı güncelle
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}






/* catchsiz ve fotogüncellemesiz hal
package com.farukayata.e_commerce2.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.data.repo.UserRepository
import com.farukayata.e_commerce2.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    init {
        loadUserProfile() // ViewModel başlatıldığında kullanıcı profilini çek
    }

    // **1. Kullanıcı Profilini Firestore’dan Al**
    fun loadUserProfile() {
        viewModelScope.launch {
            val profile = repository.getUserProfile()
            profile?.let { _userProfile.value = it }
        }
    }

    // **2. Kullanıcı Bilgilerini Güncelle**
    fun updateUserProfile(updatedData: UserProfile) {
        viewModelScope.launch {
            repository.updateUserProfile(updatedData)
            loadUserProfile() // Güncellendikten sonra verileri tekrar çek
        }
    }

    // **3. Kullanıcı Çıkış Yap**
    fun logout() {
        repository.logoutUser()
    }

    // **3. Kullanıcı Fotoğrafını Firebase Storage'a Yükle**
    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            val imageUrl = repository.uploadProfileImage(imageUri)
            if (imageUrl != null) {
                repository.updateProfileImage(imageUrl) // Firestore'daki URL'yi güncelle
                loadUserProfile() // UI'yı güncelle
            }
        }
    }

}


 */











/*
package com.farukayata.e_commerce2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.data.repo.UserRepository
import com.farukayata.e_commerce2.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> get() = _userProfile

    // Kullanıcı bilgilerini Firestore'dan çek
    fun loadUserProfile() {
        viewModelScope.launch {
            _userProfile.value = repository.getUserProfile()
        }
    }

    // Kullanıcı bilgilerini güncelle
    fun updateUserProfile(userProfile: UserProfile) {
        viewModelScope.launch {
            repository.updateUserProfile(userProfile)
            loadUserProfile() // Güncellendikten sonra tekrar yükle
        }
    }

    // Kullanıcıyı çıkış yaptır
    fun logout() {
        repository.logoutUser()
    }
}


 */