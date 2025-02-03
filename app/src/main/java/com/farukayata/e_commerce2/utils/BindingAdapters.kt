package com.farukayata.e_commerce2.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.R

object BindingAdapters {

    @BindingAdapter("app:imageUrl")
    @JvmStatic
    fun loadImage(view: ImageView, imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(view.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder1) // Yüklenirken gösterilecek resim
                .error(R.drawable.error_image1) // Hata durumunda gösterilecek resim
                .into(view)
        } else {
            view.setImageResource(R.drawable.profil_empty) // Eğer boşsa varsayılan resim
        }
    }
}
