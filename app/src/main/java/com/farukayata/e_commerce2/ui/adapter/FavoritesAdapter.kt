package com.farukayata.e_commerce2.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.databinding.CardDesignBinding
import com.farukayata.e_commerce2.model.Product

class FavoritesAdapter(
    private val onFavoriteClick: (Product) -> Unit,
    // favoriye ekleme  çıkarma için lambda
    private val onProductClick: (Product) -> Unit
    // ürün detayına geçiş için lambda
) : ListAdapter<Product, FavoritesAdapter.FavoriteViewHolder>(FavoriteDiffCallback()) {

    inner class FavoriteViewHolder(val binding: CardDesignBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = CardDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        //bu kısımda kullamak istediğimiz card designnını değiştire biliriz isteğe bağlı
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val product = getItem(position)

        holder.binding.apply {
            this.product = product //data binding kullanımı

            //ürün resmini yükledik
            Glide.with(imageViewProductCard.context)
                .load(product.image)
                .into(imageViewProductCard)

            //ürün e tıklayarak detay sayfasına geçtik
            imageViewProductCard.setOnClickListener {
                onProductClick(product)
            }

            //favori butonu görünürlüğü
            favicon1visibility.visibility = if (product.isFavorite) View.VISIBLE else View.GONE
            favicon1novisibility.visibility = if (product.isFavorite) View.GONE else View.VISIBLE

            //favori butonuna tıklama ile ekleyip çıkartmak
            favoriteContainer.setOnClickListener {
                product.isFavorite = !product.isFavorite
                onFavoriteClick(product)
                notifyItemChanged(position) // ıi güncelleme
            }
        }
    }

    //favori listemiz değişince listeyi güncellicez
    override fun submitList(list: List<Product>?) {
        super.submitList(list?.toList()) // Listeyi güncellemek için
        notifyDataSetChanged() // Listeyi zorla yenilemek için (Gerekirse çıkarılabilir)
    }

    //difutil ile liste farklarını optimize ettik
    class FavoriteDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}