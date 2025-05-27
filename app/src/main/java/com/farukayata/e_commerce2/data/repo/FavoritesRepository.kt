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
            .collection("favorites")

    // Favori ürünleri al
    suspend fun getFavorites(): List<Product> {
        return try {
            favoritesCollection.get().await().toObjects(Product::class.java)
            //Firestoredan favori ürünleri getirir.
            //Firestoredan alınan belgeleri Product modeline dönüştürdük
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Error fetching favorites", e)
            emptyList()
        }
    }

    suspend fun addFavorite(favorite: Product) {
        try {
            favoritesCollection.document(favorite.id.toString()).set(favorite).await()
            //set(favorite) => ürünleri favorilere ekler
            Log.d("FavoritesRepository", "Favorite added: ${favorite.title}, Price: ${favorite.price}")
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Error adding favorite", e)
        }
    }

    suspend fun removeFavorite(productId: String) {
        try {
            favoritesCollection.document(productId).delete().await()
        } catch (e: Exception) {
            //duruma uygun içerik eklicez aklımıza gelince
        }
    }
}