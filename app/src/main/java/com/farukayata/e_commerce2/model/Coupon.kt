package com.farukayata.e_commerce2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Coupon(
    val id: String? = null, // Kuponun benzersiz ID'si
    val code: String? = null, // Kupon kodu
    val discountAmount: Double? = null, // İndirim tutarı
    val isValid: Boolean? = true, // Geçerliliği kontrol etmek için
    val issuedDate: Date? = null // Kuponun kazanılma tarihi
) : Parcelable