package com.farukayata.e_commerce2.model

import java.io.Serializable

data class Product (
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val image: String,
    val category: String
    ): Serializable

//istersek parcelize(Intent veya Bundle ile veri aktarımı yapar) da kullana bilirdik
//sonrası için projeyi hızlandırmak adına değiştirile bilir