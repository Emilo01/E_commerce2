package com.farukayata.e_commerce2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.databinding.ItemPaymentCartBinding
import com.farukayata.e_commerce2.model.CartItem

class PaymentCartAdapter(private val cartItems: List<CartItem>) :
    RecyclerView.Adapter<PaymentCartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemPaymentCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(private val binding: ItemPaymentCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            val itemCount = item.count ?: 1
            val unitPrice = item.price ?: 0.0
            val totalPrice = unitPrice * itemCount //Toplam fiyatı hesapla (Birim fiyat x Adet)

            // 🔹 Güncellenen alanlar:
            binding.textViewProductName.text = item.title ?: "Ürün Adı Yok"
            binding.textViewProductQuantity.text = "Adet: $itemCount"
            binding.textViewProductPrice.text = "Toplam Fiyat: ${String.format("%.2f", totalPrice)} TL"

            Glide.with(binding.imageViewProduct.context)
                .load(item.image)
                .into(binding.imageViewProduct)
        }
    }
}
