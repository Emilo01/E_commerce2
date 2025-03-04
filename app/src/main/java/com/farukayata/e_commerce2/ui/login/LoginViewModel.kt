package com.farukayata.e_commerce2.ui.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.core.Response
import com.farukayata.e_commerce2.data.repo.AuthenticationRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Response<Any>>(Response.Loading)
    val loginState: StateFlow<Response<Any>> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Response.Loading
            authRepository.login(email, password).collect { response ->
                _loginState.value = response
            }
        }
    }

    fun getGoogleSignInIntent(): Intent {
        return authRepository.getGoogleSignInIntent()
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _loginState.value = Response.Loading
            authRepository.signInWithGoogle(account).collect { response ->
                _loginState.value = response
            }
        }
    }

}
