package com.farukayata.e_commerce2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val id: String? = null,
    val title: String? = null,
    val price: Double? = null,
    val description: String? = null,
    val image: String? = null,
    val category: String? = null,
    var count: Int? = 1
) : Parcelable
