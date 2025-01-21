package com.farukayata.e_commerce2.data.repo

import com.farukayata.e_commerce2.core.Response
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
//import com.farukayata.e_commerce2.data.repo.Response

class AuthenticationRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

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
}
