package com.farukayata.e_commerce2.model

import java.io.Serializable

data class Favorite(
    val productId: String = "", // Firestore'da ürün için benzersiz kimlik
    val title: String = "", // Ürün başlığı
    val price: Double = 0.0, // Ürün fiyatı
    val image: String = "", // Ürün görsel URL'si
    val category: String = "" // Ürün kategorisi
) : Serializable
