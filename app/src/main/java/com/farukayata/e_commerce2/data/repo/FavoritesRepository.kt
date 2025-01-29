package com.farukayata.e_commerce2.data.repo

import android.util.Log
import com.farukayata.e_commerce2.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FavoritesRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    // Kullanıcıya özgü favoriler koleksiyonu
    private val favoritesCollection
        get() = firestore.collection("users")   //-> sepettede bu şekilde yapı oluştur
            .document(firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not logged in"))
            .collection("favorites") //->cart items

    // Favori ürünleri al
    suspend fun getFavorites(): List<Product> {
        return try {
            favoritesCollection.get().await().toObjects(Product::class.java)
            //Firestore'dan favori ürünleri getirir.
            //Firestore'dan alınan belgeleri Product modeline dönüştürür
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Error fetching favorites", e) //logcatte hata vermedi
            emptyList()
        }
    }

    // Yeni bir favori ekle
    suspend fun addFavorite(favorite: Product) {
        try {
            favoritesCollection.document(favorite.id.toString()).set(favorite).await()
            //set(favorite) => ürünleri favorilere ekler
            Log.d("FavoritesRepository", "Favorite added: ${favorite.title}, Price: ${favorite.price}")
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Error adding favorite", e)
        }
    }

    // Favori bir ürünü kaldır
    suspend fun removeFavorite(productId: String) {
        try {
            favoritesCollection.document(productId).delete().await()
        } catch (e: Exception) {
            // Hata loglaması yapılabilir
        }
    }
}










/*
package com.farukayata.e_commerce2.data.repo

import com.farukayata.e_commerce2.model.Favorite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FavoritesRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val userId: String
        get() = firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    private val favoritesCollection
        get() = firestore.collection("users").document(userId).collection("favorites")

    suspend fun addFavorite(favorite: Favorite) {
        favoritesCollection.document(favorite.productId).set(favorite).await()
    }

    suspend fun removeFavorite(productId: String) {
        favoritesCollection.document(productId).delete().await()
    }

    suspend fun getFavorites(): List<Favorite> {
        return try {
            favoritesCollection.get().await().toObjects(Favorite::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}


 */