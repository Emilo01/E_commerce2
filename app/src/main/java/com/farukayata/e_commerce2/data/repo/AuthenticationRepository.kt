package com.farukayata.e_commerce2.data.repo

import android.content.Context
import android.content.Intent
import com.farukayata.e_commerce2.core.Response
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
//import com.farukayata.e_commerce2.data.repo.Response

class AuthenticationRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val context: Context
) {

    private val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("755896592492-v8upuudbkenrgb6v6pb9ebkkett2221a.apps.googleusercontent.com") // **Firebase projenin Web Client ID’sini buraya yaz!**
        .requestEmail()
        .build()

    fun register(email: String, password: String): Flow<Response<Any>> = flow {
        emit(Response.Loading)
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            //firebase ile kullaıcı kayıt ettik
            emit(Response.Success("User registered successfully"))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "An error occurred"))
        }
    }

    fun login(email: String, password: String): Flow<Response<Any>> = flow {
        emit(Response.Loading)
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Response.Success("Login successful"))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "An error occurred"))
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) = flow {
        emit(Response.Loading)
        try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()

            if (authResult.user != null) {
                emit(Response.Success("Google Sign-In successful: ${authResult.user!!.email}"))
                println("Firebase Auth başarılı! Kullanıcı: ${authResult.user!!.email}")
            } else {
                emit(Response.Error("Google Sign-In başarısız! Kullanıcı nesnesi boş."))
                println("Google Sign-In başarısız! Kullanıcı nesnesi null döndü.")
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Google Sign-In failed"))
            println("Google Sign-In başarısız! Hata: ${e.message}")
        }
    }



    fun getGoogleSignInIntent(): Intent {
        val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
        return googleSignInClient.signInIntent
    }


}
