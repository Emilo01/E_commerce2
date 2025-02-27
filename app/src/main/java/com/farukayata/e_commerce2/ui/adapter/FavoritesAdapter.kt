package com.farukayata.e_commerce2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.databinding.CardDesignBinding
import com.farukayata.e_commerce2.databinding.FavoriresCardDesignBinding
import com.farukayata.e_commerce2.model.Product

class FavoritesAdapter(
    private val onRemoveClick: (String) -> Unit,
    private val onProductClick: (Product) -> Unit // Ürün tıklaması için lambda
) : ListAdapter<Product, FavoritesAdapter.FavoriteViewHolder>(FavoriteDiffCallback()) {

    //FavoriteDiffCallback: DiffUtil ile iki listeyi karşılaştırmak ve farkları belirlemek için

    inner class FavoriteViewHolder(val binding: FavoriresCardDesignBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = FavoriresCardDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        //CardDesignBinding => favorite card design binding
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = getItem(position)

        holder.binding.apply {
            textViewFavoritesTitle.text = favorite.title // Ürün başlığı
            //textviewları felann değiş adını
            textViewFavoritesPrice.text = String.format("%.2f TL", favorite.price) // Ürün fiyatı
            Glide.with(imageViewFavoritesCard.context).load(favorite.image).into(imageViewFavoritesCard) // Ürün görseli

            // Ürün tıklama olayı
            root.setOnClickListener {
                onProductClick(favorite) // Ürün tıklandığında detail sayfasına geçişi tetikler
            }

            //Remove düğmesi
            buttonShop.text = "Remove"
            buttonShop.setOnClickListener {
                onRemoveClick(favorite.id.toString()) // Ürünü favorilerden kaldır
            }
        }
    }

    // DiffUtil.ItemCallback implementasyonu
    //ListAdapter ile birlikte kullanılır. Amacı, iki liste arasındaki farkları optimize bir şekilde belirlemek ve yalnızca değişen öğeleri güncelleyerek performansı artırmaktır.

    class FavoriteDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }
        //Eski liste ile yeni listedeki öğelerin productId değerleri karşılaştırılır.
        //Aynı kimlik değerine sahipse, bu iki öğenin aynı olduğu kabul edilir.

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
        //Eski ve yeni öğeler tamamen eşitse (içerikleri aynıysa), bu iki öğe aynı kabul edilir. Kotlin'deki == operatörü,
        // data class'ın otomatik olarak ürettiği equals fonksiyonunu çağırır, yani tüm alanları karşılaştırır.
    }
}


