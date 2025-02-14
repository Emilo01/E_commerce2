package com.farukayata.e_commerce2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.databinding.CartItemDesignBinding
import com.farukayata.e_commerce2.model.CartItem

class CartAdapter(
    private val onRemoveClick: (String) -> Unit,
    private val onIncreaseClick: (CartItem) -> Unit,
    private val onDecreaseClick: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    inner class CartViewHolder(private val binding: CartItemDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.textViewCartTitle.text = cartItem.title
            binding.textViewCartPrice.text = "${cartItem.price} TL"
            binding.textViewCartCount.text = cartItem.count.toString()

            Glide.with(binding.imageViewCartItem.context)
                .load(cartItem.image)
                .into(binding.imageViewCartItem)

            // Adet artırma
            binding.buttonIncrease.setOnClickListener {
                onIncreaseClick(cartItem)
            }

            // Adet azaltma
            binding.buttonDecrase.setOnClickListener {
                onDecreaseClick(cartItem)
            }

            // Silme butonu
            binding.buttonRemove.setOnClickListener {
                onRemoveClick(cartItem.id.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemDesignBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // **RecyclerView Performansını Artırmak için DiffUtil**
    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}
