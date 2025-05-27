package com.farukayata.e_commerce2.model

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties // Firestore'dan ekstra alanlar geldiğinde hata almamak için -- !!geri dön !!
@Parcelize
data class Product(
    val id: Int? = null,
    val title: String? = null,
    val price: Double? = null,
    val description: String? = null,
    val image: String? = null,
    val category: String? = null,
    var isFavorite: Boolean = false,
    @SerializedName("rating") val rating: ProductRating? = ProductRating() // Varsayılan değer atandı
) : Parcelable

@Parcelize
data class ProductRating(
    @SerializedName("rate") val rate: Double = 0.0,
    @SerializedName("count") val count: Int = 0
) : Parcelable {
    constructor() : this(0.0, 0) // Firestoreun deserialize edebilmesi için parametresiz constructor eklendi
}
