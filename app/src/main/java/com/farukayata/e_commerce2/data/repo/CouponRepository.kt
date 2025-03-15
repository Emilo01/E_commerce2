package com.farukayata.e_commerce2.data.repo

import android.util.Log
import com.farukayata.e_commerce2.model.Coupon
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class CouponRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    // Kullanıcının tanımlı kuponlarını alıp süresi dolan kupoları sildik
    suspend fun getUserCoupons(): List<Coupon> {
        val userId = firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException("User not logged in")

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("coupons")
            .get()
            .await()

        val coupons = snapshot.documents.mapNotNull { document ->
            val coupon = document.toObject(Coupon::class.java)
            if (coupon?.code != null && coupon.discountAmount != null) {
                coupon
            } else {
                null
            }
        }

        //süresi dolan kuponları filtreleyip ve Firestoredan sildik
        val validCoupons = coupons.filter { isCouponStillValid(it) }
        val expiredCoupons = coupons.filterNot { isCouponStillValid(it) }

        // Firestore'dan süresi dolan kuponları sildik
        expiredCoupons.forEach { coupon ->
            deleteExpiredCoupon(userId, coupon.id)
        }

        return validCoupons
    }

    //firestoredan süresi dolan kuponları silme fonksiyonu
    private suspend fun deleteExpiredCoupon(userId: String, couponId: String?) {
        if (couponId == null) return

        try {
            firestore.collection("users")
                .document(userId)
                .collection("coupons")
                .document(couponId)
                .delete()
                .await()
            Log.d("CouponRepository", "Süresi dolan kupon silindi: $couponId")
        } catch (e: Exception) {
            Log.e("CouponRepository", "Kupon silinirken hata oluştu: ${e.localizedMessage}")
        }
    }

    //kuponun süresinin dolup dolmadığını kontrol ettik
    private fun isCouponStillValid(coupon: Coupon): Boolean {
        val issuedDate = coupon.issuedDate ?: return false
        val calendar = Calendar.getInstance()
        calendar.time = issuedDate
        calendar.add(Calendar.DAY_OF_YEAR, 7) // + 7 gün

        val expiryDate = calendar.time
        val currentDate = Date()

        return currentDate.before(expiryDate)
        //bugün tarihi ile kupon so kulllanma tarihi compare ettik
    }

    // Kullanıcıya kupon ekleme (issuedDate eklenerek)
    suspend fun addCoupon(coupon: Coupon) {
        val userId = firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException("User not logged in")

        try {
            val couponRef = firestore.collection("users")
                .document(userId)
                .collection("coupons")
                .document()

            val newCoupon = coupon.copy(issuedDate = Date()) //issuedDate alanı ekleniyor
            couponRef.set(newCoupon).await()

            Log.d("CouponRepository", "Kupon başarıyla eklendi: $userId")
        } catch (e: Exception) {
            Log.e("CouponRepository", "Kupon eklenirken hata oluştu: ${e.localizedMessage}")
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





/*
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


 */