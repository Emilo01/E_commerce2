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
    private val storage: FirebaseStorage // kullanıcı resimi yüklemek için storage eklendi
) {

    private val userCollection get() = firestore.collection("users").document(auth.currentUser?.uid ?: "")

    //Kullanıcı Bilgilerini Getir
    suspend fun getUserProfile(): UserProfile? {
        return try {
            val snapshot = userCollection.get().await()
            snapshot.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    //Kullanıcı Bilgilerini Güncelle
    suspend fun updateUserProfile(userProfile: UserProfile) {
        try {
            userCollection.set(userProfile).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Firebase Storage’a Fotoğraf Yükle ve URL Döndür
    suspend fun uploadProfileImage(imageUri: Uri): String? {
        return try {
            val userId = auth.currentUser?.uid ?: return null
            val storageRef = storage.reference.child("profile_images/$userId.jpg")

            storageRef.putFile(imageUri).await() // Resmi Storage'a yükle

            val downloadUrl = storageRef.downloadUrl.await().toString() // Resmi indir
            //storageRef.downloadUrl.await().toString() // URL'yi döndür

            println("Firebase Storage'a resim yüklendi: $downloadUrl")
            downloadUrl// başarılı olması durumunda URL ti bize dönndürcek

        } catch (e: Exception) {
            e.printStackTrace()
            println("Error Firebase Storage'a resim yüklenemedi")
            null
        }
    }

    //Kullanıcı Fotoğrafını Güncelle
    suspend fun updateProfileImage(imageUrl: String) {
        try {
            userCollection.update("profileImageUrl", imageUrl).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //Kullanıcıyı Çıkış Yaptır
    fun logoutUser() {
        auth.signOut()
    }
}
