package com.farukayata.e_commerce2.data.repo

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.farukayata.e_commerce2.model.Order
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val TAG = "OrderRepository"

    private val ordersCollection
        get() = firestore.collection("users")
            .document(getCurrentUserId())
            .collection("orders")

    //Kullanıcı ID’yi güvenli bir şekilde almak için
    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("Kullanıcı giriş yapmamış!")
    }

    //Siparişi Firestore’a kaydetme
    suspend fun saveOrder(order: Order) {
        try {
            val userId = getCurrentUserId()
            val orderPath = "users/$userId/orders/${order.id}"

            Log.d(TAG, "Firestore’a şu yola sipariş kaydediliyor: $orderPath")
            Log.d(TAG, "Firebase’e sipariş kaydediliyor, Kullanıcı ID: $userId")

            val orderRef = firestore.collection("users")
                .document(userId)
                .collection("orders")
                .document(order.id)

            orderRef.set(order).await()

            Log.d(TAG, "Sipariş başarıyla kaydedildi: ${order.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Sipariş kaydedilirken hata oluştu: ${e.localizedMessage}")
        }
    }

    //Firestore’dan siparişleri çekme
    suspend fun getOrdersFromFirebase(): List<Order> {
        return try {
            val userId = getCurrentUserId()
            Log.d(TAG, "Siparişler Firestore'dan alınıyor, Kullanıcı ID: $userId")

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("orders")
                .get()
                .await()

            val orders = snapshot.documents.mapNotNull { it.toObject<Order>() }

            Log.d(TAG, "${orders.size} sipariş başarıyla alındı.")
            orders
        } catch (e: Exception) {
            Log.e(TAG, "Siparişleri alırken hata oluştu: ${e.localizedMessage}")
            emptyList()
        }
    }
}









/*
package com.farukayata.e_commerce2.data.repo

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.farukayata.e_commerce2.model.Order
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val ordersCollection
        get() = firestore.collection("users")
            .document(auth.currentUser?.uid ?: throw IllegalStateException("User not logged in"))
            .collection("orders")

    /** Siparişi Firestore'a kaydetme işlemi */
    suspend fun saveOrder(order: Order) {
        try {
            ordersCollection.document(order.id).set(order).await()
            Log.d("Firestore", "Sipariş başarıyla kaydedildi: ${order.id}")
        } catch (e: Exception) {
            Log.e("Firestore", "Sipariş kaydedilirken hata oluştu: ${e.localizedMessage}")
        }
    }

    /** Firestore'dan siparişleri çekme işlemi */
    suspend fun getOrdersFromFirebase(): List<Order> {
        return try {
            val snapshot = ordersCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject<Order>() }
        } catch (e: Exception) {
            Log.e("Firestore", "Siparişleri alırken hata oluştu: ${e.localizedMessage}")
            emptyList()
        }
    }
}


 */