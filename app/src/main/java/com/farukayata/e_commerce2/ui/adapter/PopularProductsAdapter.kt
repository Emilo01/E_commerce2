package com.farukayata.e_commerce2.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.CardDesignBinding
import com.farukayata.e_commerce2.model.Product

class PopularProductsAdapter(
    private val context: Context,
    private val onProductClick: (Product) -> Unit,
    private val onFavoriteClick: (Product) -> Unit,
    private val onRemoveFavoriteClick: (Product) -> Unit
) : ListAdapter<Product, PopularProductsAdapter.CardDesignViewHolder>(ProductDiffCallback()) {

    inner class CardDesignViewHolder(val binding: CardDesignBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardDesignViewHolder {
        val binding: CardDesignBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.card_design, parent, false
        )
        return CardDesignViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardDesignViewHolder, position: Int) {
        val product = getItem(position)
        holder.binding.product = product

        // Ürün resmini Glide ile yükleme
        Glide.with(holder.binding.imageViewProductCard.context)
            .load(product.image)
            .into(holder.binding.imageViewProductCard)

        // Ürün kartına tıklanınca detay sayfasına yönlendirme
        holder.binding.imageViewProductCard.setOnClickListener {
            onProductClick(product)
        }

        // Favori butonu yönetimi
        holder.binding.favicon1visibility.visibility = if (product.isFavorite) View.VISIBLE else View.GONE
        holder.binding.favicon1novisibility.visibility = if (product.isFavorite) View.GONE else View.VISIBLE

        // Favori butonuna tıklanınca favoriye ekleme / çıkarma işlemi
        holder.binding.favoriteContainer.setOnClickListener {
            if (product.isFavorite) {
                onRemoveFavoriteClick(product) // Eğer favorideyse çıkart
            } else {
                onFavoriteClick(product) // Eğer favoride değilse ekle
            }
            notifyItemChanged(position) // UI'yi güncelle
        }
    }

    // DiffUtil ile liste değişikliklerini optimize etme
    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}









/*
package com.farukayata.e_commerce2.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.CardDesignBinding
import com.farukayata.e_commerce2.model.Product

class PopularProductsAdapter(
    private val context: Context,
    private val onProductClick: (Product) -> Unit,
    private val onFavoriteClick: (Product) -> Unit
) : ListAdapter<Product, PopularProductsAdapter.CardDesignViewHolder>(ProductDiffCallback()) {

    inner class CardDesignViewHolder(val binding: CardDesignBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardDesignViewHolder {
        val binding: CardDesignBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.card_design, parent, false
        )
        return CardDesignViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardDesignViewHolder, position: Int) {
        val product = getItem(position)
        holder.binding.product = product

        Glide.with(holder.binding.imageViewProductCard.context)
            .load(product.image)
            .into(holder.binding.imageViewProductCard)

        //ürün kartına tıklanınca detay sayfasına gitsin
        holder.binding.cardViewEcommorceProduct.setOnClickListener {
            onProductClick(product)
        }

        holder.binding.buttonShop.apply {
            text = if (product.isFavorite) "Added to Favorites" else "Add to Favorites" // Eğer favoriye eklendiyse UI'da göster

            setOnClickListener {
                onFavoriteClick(product)
                product.isFavorite = true // Güncellenen UI için
                notifyItemChanged(position) // Güncelleme yaparak yeni favori durumunu göster
            }
        }

    }



    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}


 */