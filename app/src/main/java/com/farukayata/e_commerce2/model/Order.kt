package com.farukayata.e_commerce2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable
