package com.farukayata.e_commerce2.data.entity

import java.io.Serializable

data class Commerce_Products(var id: Int,
                             var title:String,
                             var image: String,
                             var price: Double) :
    Serializable {
    //serializable home dan -> detail a veri göndermede gerekli
    //proje başta localden veri alırken burayı kullanıyordum şu ann geçersiz
}