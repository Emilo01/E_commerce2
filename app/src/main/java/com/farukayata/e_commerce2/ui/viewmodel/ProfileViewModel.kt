package com.farukayata.e_commerce2.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.data.repo.UserRepository
import com.farukayata.e_commerce2.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    private val _logoutEvent = MutableLiveData<Boolean>()
    // Logout işlemi için event tetiklicek
    val logoutEvent: LiveData<Boolean> get() = _logoutEvent

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> get() = _userEmail


    init {
        loadUserProfile() // ViewModel başlatıldığında kullanıcı profilini çek
        loadUserEmail()
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

    private fun loadUserEmail() {
        val email = FirebaseAuth.getInstance().currentUser?.email
        _userEmail.value = email ?: "E-posta bulunamadı"
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
        _logoutEvent.value = true // Logout işlemi tamamlandığını tetikle
    }

    //Kullanıcı Fotoğrafını Firebase Storage a Yükle
    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            try {
                val imageUrl = repository.uploadProfileImage(imageUri)
                if (imageUrl != null) {
                    repository.updateProfileImage(imageUrl)
                    //bi alt satıra gerek kalmadı profile fragmetta onresume kullanınca
                    //_userProfile.value = _userProfile.value?.copy(profileImageUrl = imageUrl) // UI güncellemesi
                    //Firestore'daki URL'yi güncelle
                    loadUserProfile() //UI’yı güncelle
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
