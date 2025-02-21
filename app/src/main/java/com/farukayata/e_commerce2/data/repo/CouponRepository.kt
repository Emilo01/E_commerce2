package com.farukayata.e_commerce2.data.repo

import android.util.Log
import com.farukayata.e_commerce2.model.Coupon
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CouponRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    // Kullanıcının tanımlı kuponlarını alır
    suspend fun getUserCoupons(): List<Coupon> {
        val userId = firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException("User not logged in")

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("coupons")
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->
            val coupon = document.toObject(Coupon::class.java)
            // Kuponun kodu ve indirim tutarı null olmamalıdır
            if (coupon?.code != null && coupon.discountAmount != null) {
                coupon
            } else {
                null
            }
        }
    }

    // Kullanıcıya kupon ekle
//    suspend fun addCoupon(coupon: Coupon) {
//        val userId = firebaseAuth.currentUser?.uid
//            ?: throw IllegalStateException("User not logged in")
//
//        // Null kontrolü ekledik: coupon.id alanı null olamaz
//        val couponId = coupon.id?.takeIf { it.isNotBlank() } ?: throw IllegalArgumentException("Coupon ID is invalid")
//
//        try {
//            firestore.collection("users")
//                .document(userId)
//                .collection("coupons")
//                .document(couponId)
//                .set(coupon)
//                .await()
//            Log.d("CouponRepository", "Coupon successfully added for user: $userId")
//        } catch (e: Exception) {
//            Log.e("CouponRepository", "Error saving coupon: ${e.localizedMessage}")
//            throw e
//        }
//    }

    //getter setter ile yapınca order viewmodellde de ufak bir güncellem ile
    //kupon ekliyor artık
    // Kullanıcıya kupon ekler - Otomatik ID kullanarak
    suspend fun addCoupon(coupon: Coupon) {
        val userId = firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException("User not logged in")

        try {
            // Firestore'a kuponu otomatik ID ile ekliyoruz
            val couponRef = firestore.collection("users")
                .document(userId)
                .collection("coupons")
                .document() // Bu, otomatik bir ID oluşturur
            couponRef.set(coupon).await()  // Firestore'a kuponu ekle
            Log.d("CouponRepository", "Coupon successfully added for user: $userId")
        } catch (e: Exception) {
            Log.e("CouponRepository", "Error saving coupon: ${e.localizedMessage}")
        }
    }

    // Kuponu geçerli mi diye kontrol eder
    suspend fun validateCoupon(code: String): Coupon? {
        val coupons = getUserCoupons()
        return coupons.find { coupon ->
            coupon.code == code && (coupon.isValid ?: false) && coupon.discountAmount != null
        }
    }

}
