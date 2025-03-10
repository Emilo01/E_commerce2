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

class EcommorceAdapter(
    private val context: Context,
    private val onProductClick: (Product) -> Unit,
    private val onFavoriteClick: (Product) -> Unit,
    private val onRemoveFavoriteClick: (Product) -> Unit
) : ListAdapter<Product, EcommorceAdapter.CardDesignViewHolder>(ProductDiffCallback()) {

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

        // Ürün kartına tıklama (detay sayfasına geçiş)
        holder.binding.imageViewProductCard.setOnClickListener {
            onProductClick(product)
        }

        // Favori butonu yönetimi
        holder.binding.favicon1visibility.visibility = if (product.isFavorite) View.VISIBLE else View.GONE
        holder.binding.favicon1novisibility.visibility = if (product.isFavorite) View.GONE else View.VISIBLE

        holder.binding.favoriteContainer.setOnClickListener {
            if (product.isFavorite) {
                onRemoveFavoriteClick(product) // Eğer favorideyse çıkart
            } else {
                onFavoriteClick(product) // Eğer favoride değilse ekle
            }
            notifyItemChanged(position) // UI'yi güncelle
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








/*
package com.farukayata.e_commerce2.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.CardDesignBinding
import com.farukayata.e_commerce2.model.Product
import com.farukayata.e_commerce2.ui.fragment.HomeFragmentDirections

class EcommorceAdapter(
    private val context: Context, //context vermek mantıklı değil ve clikği içerde tanımlamak lazım
    private val onProductClick: (Product) -> Unit, // Ürün tıklama için lambda
    private val onFavoriteClick: (Product) -> Unit // Favorilere ekleme için lambda fonksiyonu
) : ListAdapter<Product, EcommorceAdapter.CardDesignViewHolder>(ProductDiffCallback()) {

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

        //daha iyi performans için context yerine imageView.context ile değiştik
        Glide.with(holder.binding.imageViewProductCard.context)//holder.binding.imageViewProductCard.context -- adapterin connnstraktırındacontext tutulmaz
            .load(product.image)
            .into(holder.binding.imageViewProductCard)

        // Ürüne tıklama olayını tanımladık (detail sayfasına geçiş için)
        holder.binding.cardViewEcommorceProduct.setOnClickListener {
            onProductClick(product) // Lambda ile geçişi tetikle.  artık burdasetonclick ortak olu

        //yukarıda ki gibi bıraktık bu sayede sadece home için değil categoryspecial içinde kullandık
        //val action = HomeFragmentDirections.detailGecis(product)
        //Navigation.findNavController(it).navigate(action)
        }

        // Favorilere ekleme butonuna tıklama

        holder.binding.buttonShop.apply {
            text = if (product.isFavorite) "Added to Favorites" else "Add to Favorites" // Eğer favoriye eklendiyse UI'da göster

            setOnClickListener {
                onFavoriteClick(product)
                product.isFavorite = true // Güncellenen UI için
                notifyItemChanged(position) // Güncelleme yaparak yeni favori durumunu göster
            }
        }

//        holder.binding.buttonShop.text = "Add to Favorites" // Buton yazısını favori işlemi için değiştiriyoruz
//        holder.binding.buttonShop.setOnClickListener {
//            onFavoriteClick(product) // Lambda fonksiyonu üzerinden favorilere ekleme işlemini tetikle
//        }
    }

    // DiffUtil: ListAdapter performansını optimize etmek için gerekli
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