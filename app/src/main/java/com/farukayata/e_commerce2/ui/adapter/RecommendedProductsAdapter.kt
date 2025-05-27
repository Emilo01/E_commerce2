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

class RecommendedProductsAdapter(
    private val context: Context,
    private val onProductClick: (Product) -> Unit,
    private val onFavoriteClick: (Product) -> Unit,
    private val onRemoveFavoriteClick: (Product) -> Unit
) : ListAdapter<Product, RecommendedProductsAdapter.CardDesignViewHolder>(ProductDiffCallback()) {

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

        //ürün görselini yükleme
        Glide.with(holder.binding.imageViewProductCard.context)
            .load(product.image)
            .into(holder.binding.imageViewProductCard)

        holder.binding.imageViewProductCard.setOnClickListener {
            onProductClick(product)
        }

        holder.binding.favicon1visibility.visibility = if (product.isFavorite) View.VISIBLE else View.GONE
        holder.binding.favicon1novisibility.visibility = if (product.isFavorite) View.GONE else View.VISIBLE

        holder.binding.favoriteContainer.setOnClickListener {
            //burda ilk girerken true olan isfavorite true ttekrar false e düşüyor ????????????? sonra true oluyor
            if (product.isFavorite) {
                onRemoveFavoriteClick(product)
            } else {
                onFavoriteClick(product)
            }
            notifyItemChanged(position) // ui güncelle
        }
    }

    // DiffUtil kullanarak performans artırıyoruz
    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
