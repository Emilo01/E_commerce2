package com.farukayata.e_commerce2.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.core.Response
import com.farukayata.e_commerce2.data.repo.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository
) : ViewModel() {

    private val _signUpState = MutableStateFlow<Response<Any>>(Response.Loading) // Başlangıçta Loading
    val signUpState: StateFlow<Response<Any>> = _signUpState

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _signUpState.value = Response.Loading // İşlem başladı
                authRepository.register(email, password).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            _signUpState.value = Response.Success("Registration successful")
                        }
                        is Response.Error -> {
                            _signUpState.value = Response.Error(response.message ?: "Unknown error")
                        }
                        else -> {
                            _signUpState.value = Response.Error("Unexpected response")
                        }
                    }
                }
            } catch (e: Exception) {
                _signUpState.value = Response.Error(e.message ?: "Exception occurred")
            }
        }
    }
}
