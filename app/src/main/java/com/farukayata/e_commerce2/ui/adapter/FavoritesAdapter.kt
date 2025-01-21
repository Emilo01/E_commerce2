package com.farukayata.e_commerce2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.databinding.CardDesignBinding
import com.farukayata.e_commerce2.model.Favorite
import com.farukayata.e_commerce2.model.Product

class FavoritesAdapter(
    private val onRemoveClick: (String) -> Unit
) : ListAdapter<Product, FavoritesAdapter.FavoriteViewHolder>(FavoriteDiffCallback()) {

    inner class FavoriteViewHolder(val binding: CardDesignBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = CardDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        //CardDesignBinding => favorite card design binding
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = getItem(position)

        holder.binding.apply {
            textViewProductTitle.text = favorite.title // Ürün başlığı
            //textviewları felann değiş adını
            textViewProductPrice.text = String.format("%.2f TL", favorite.price) // Ürün fiyatı
            Glide.with(imageViewProductCard.context).load(favorite.image).into(imageViewProductCard) // Ürün görseli

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












/*
package com.farukayata.e_commerce2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.databinding.CardDesignBinding
import com.farukayata.e_commerce2.model.Favorite

class FavoritesAdapter(
    private val favoriteList: List<Favorite>,
    private val onRemoveClick: (String) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(val binding: CardDesignBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = CardDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favoriteList[position]

        holder.binding.apply {
            textViewProductTitle.text = favorite.title // Ürün başlığı
            //textViewProdutcPrice.text = "${favorite.price} TL" // Ürün fiyatı
            textViewProductPrice.text = String.format("%.2f TL", favorite.price) // Ürün fiyatı
            Glide.with(imageViewProductCard.context).load(favorite.image).into(imageViewProductCard) // Ürün görseli

            // "Add to Cart" butonunu "Remove" olarak değiştirme
            buttonShop.text = "Remove"
            buttonShop.setOnClickListener {
                onRemoveClick(favorite.productId) // Ürünü favorilerden kaldır
            }
        }
    }

    override fun getItemCount(): Int = favoriteList.size
}



 */








/*
package com.farukayata.e_commerce2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.databinding.CardDesignBinding
import com.farukayata.e_commerce2.model.Favorite

class FavoritesAdapter(
    private val favoriteList: List<Favorite>,
    private val onRemoveClick: (String) -> Unit // Favoriden kaldırma işlemi için callback
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(val binding: CardDesignBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = CardDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favoriteList[position]

        // Verileri bağlama
        holder.binding.apply {
            textViewProdutcPrice.text = "${favorite.price} TL"
            textViewTitle.text = favorite.title
            Glide.with(imageViewProductCard.context).load(favorite.image).into(imageViewProductCard)

            // Kaldırma butonuna tıklama
            buttonShop.setOnClickListener {
                onRemoveClick(favorite.productId)
            }
        }
    }

    override fun getItemCount(): Int = favoriteList.size
}


 */


/*
package com.farukayata.e_commerce2.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.CardDesignBinding
import com.farukayata.e_commerce2.model.Favorite

class FavoritesAdapter(private val context: Context) :
    ListAdapter<Favorite, FavoritesAdapter.FavoritesViewHolder>(FavoritesDiffCallback()) {

    inner class FavoritesViewHolder(val binding: CardDesignBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding: CardDesignBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.card_design,
            parent,
            false
        )
        return FavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val favorite = getItem(position)
        holder.binding.product = favorite // CardDesign ile favori ürünü bağladık
    }
}

class FavoritesDiffCallback : DiffUtil.ItemCallback<Favorite>() {
    override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
        return oldItem == newItem
    }
}


 */