package com.farukayata.e_commerce2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product (
    val id: Int?= null,
    val title: String?= null,
    val price: Double?= null,
    val description: String?= null,
    val image: String?= null,
    val category: String?= null
    ): Parcelable

//istersek parcelize(Intent veya Bundle ile veri aktarımı yapar) da kullana bilirdik
//sonrası için projeyi hızlandırmak adına değiştirile bilir