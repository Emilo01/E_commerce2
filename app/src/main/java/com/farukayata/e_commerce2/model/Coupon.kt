package com.farukayata.e_commerce2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Coupon(
    val id: String? = null,
    val code: String? = null,
    val discountAmount: Double? = null,
    val isValid: Boolean? = true,
    val issuedDate: Date? = null //kupon ekleme tarihi
) : Parcelable