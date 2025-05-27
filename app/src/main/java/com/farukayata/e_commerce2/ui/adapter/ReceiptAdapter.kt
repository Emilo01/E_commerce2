package com.farukayata.e_commerce2.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.databinding.ItemReceiptBinding
import com.farukayata.e_commerce2.model.Order
import com.farukayata.e_commerce2.model.CartItem

class ReceiptAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder>() {

    //Siparişlerdeki tüm ürünleri tek bir listeye dönüştür
    private val expandedOrderList = orders.flatMap { order ->
        order.items.map { item -> Pair(order, item) } // Her siparişi ve ürünleri birlikte tut
        //şu ada orderı kullamadığımız için gereksiz pair kullanımı vaar
    }
    //istersek orderı iptal edip şu şekilde de yazabiliriz yukarısıı
    //private val expandedOrderList = orders.flatMap { it.items }

    class ReceiptViewHolder(private val binding: ItemReceiptBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order, item:CartItem) {
            //order şu anda kullanılmıyor ama dönüşüm amaçlı kalmasıda şu an problem yok
            // ürün bilgileri
            Glide.with(binding.imageViewProduct.context)
                .load(item.image)
                .into(binding.imageViewProduct)

            binding.textViewProductName.text = item.title ?: "Ürün Adı Yok"
            binding.textViewProductQuantity.text = "Adet: ${item.count ?: 1}"
            binding.textViewProductPrice.text = "Birim Fiyatı: ${item.price ?: 0.0} TL"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val binding = ItemReceiptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReceiptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val (order, item) = expandedOrderList[position] //her siparişten gelen ürünü al
        //burda da gereksiz pair kullanımı var order kullanmadığımız içim
        holder.bind(order, item)
    }

    override fun getItemCount(): Int {
        Log.d("ReceiptAdapter", "Adapter içindeki ürün sayısı: ${expandedOrderList.size}")
        return expandedOrderList.size
    }
}