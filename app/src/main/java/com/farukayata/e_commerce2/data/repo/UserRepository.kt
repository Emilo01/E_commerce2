package com.farukayata.e_commerce2.data.repo

import android.net.Uri
import com.farukayata.e_commerce2.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) {

    private val userCollection get() = firestore.collection("users").document(auth.currentUser?.uid ?: "")

    //kullannıcı bilgisi
    suspend fun getUserProfile(): UserProfile? {
        return try {
            val snapshot = userCollection.get().await()
            snapshot.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    //kullanıcı bilgi güncelledik
    suspend fun updateUserProfile(userProfile: UserProfile) {
        try {
            userCollection.set(userProfile).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //storage a foto yükledik ve url yi döndürdük
    suspend fun uploadProfileImage(imageUri: Uri): String? {
        return try {
            val userId = auth.currentUser?.uid ?: return null
            val storageRef = storage.reference.child("profile_images/$userId.jpg")

            storageRef.putFile(imageUri).await()

            val downloadUrl = storageRef.downloadUrl.await().toString()

            println("Firebase Storage'a resim yüklendi: $downloadUrl")
            downloadUrl// başarılı olması durumunda URL ti bize dönndürcek

        } catch (e: Exception) {
            e.printStackTrace()
            println("Error Firebase Storage'a resim yüklenemedi")
            null
        }
    }

    //profil foto güncelledik
    suspend fun updateProfileImage(imageUrl: String) {
        try {
            userCollection.update("profileImageUrl", imageUrl).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun logoutUser() {
        auth.signOut()
    }
}
