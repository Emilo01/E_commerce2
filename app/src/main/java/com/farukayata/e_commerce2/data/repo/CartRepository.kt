package com.farukayata.e_commerce2.data.repo

import android.util.Log
import com.farukayata.e_commerce2.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CartRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val cartCollection
        get() = firestore.collection("users")   //-> sepettede bu şekilde yapı oluştur  /-> get kısmındaki senaryoyu öğren
            .document(firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not logged in"))
            .collection("cartItems") //->car

    // **1. Firestore'dan Sepetteki Ürünleri Çek**
    suspend fun getCartItems(): List<CartItem> {
        return try {
            val snapshot = cartCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject(CartItem::class.java) } // Firestore'dan gelen veriyi model'e çevir
        } catch (e: Exception) {
            emptyList() // Hata olursa boş liste döndür
        }
    }

    // **2. Firestore'a Ürün Ekle / Güncelle**
    suspend fun addToCart(cartItem: CartItem) {
        try {
            val documentRef = cartCollection.document(cartItem.id.toString()) // Ürün ID'sine göre doküman oluştur
            val existingItem = documentRef.get().await() // Mevcut ürünü kontrol et

            if (existingItem.exists()) {
                // Ürün zaten sepette, sadece count artır
                //documentRef.update("count", cartItem.count?.plus(1)).await()

                //şeklinde de yazıla bilir yukarıdan farklı olarak
                val currentCount = existingItem.getLong("count") ?: 0
                documentRef.update("count", currentCount + 1).await()
                Log.d("Firestore", "Ürün zaten var, count artırıldı: ${cartItem.id}")
                //Mevcut count değerini Firestore'dan çekerek artırırsak, yanlışlıkla null artırma riskini önlemiş olduk

            } else {
                // Ürün sepette yoksa yeni ekle
                documentRef.set(cartItem).await()
                Log.d("Firestore", "Yeni ürün sepete eklendi: ${cartItem.id}")
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Sepete ekleme hatası: ${e.localizedMessage}")
            e.printStackTrace()
        }
    }

    // **3. Sepetten Ürünü Sil**
    suspend fun removeFromCart(productId: String) {
        try {
            cartCollection.document(productId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // **4. Sepetteki Ürünün Adedini Güncelle**
    suspend fun updateItemCount(productId: String, newCount: Int) {
        try {
            if (newCount > 0) {
                cartCollection.document(productId).update("count", newCount).await()
            } else {
                // Eğer ürünün adedi 0'a düştüyse, sepetten tamamen sil
                removeFromCart(productId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //sepetteki ürünler alındıktan sonra tüm ürünleri silcek
    suspend fun clearCart() {
        try {
            val cartItems = cartCollection.get().await()
            for (document in cartItems.documents) {
                document.reference.delete().await()
            }
            Log.d("Firestore", "Sepet başarıyla temizlendi")
        } catch (e: Exception) {
            Log.e("Firestore", "Sepeti temizlerken hata oluştu: ${e.localizedMessage}")
            e.printStackTrace()
        }
    }

}
