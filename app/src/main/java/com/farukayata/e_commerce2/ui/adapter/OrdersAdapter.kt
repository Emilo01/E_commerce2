package com.farukayata.e_commerce2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.ItemOrderBinding
import com.farukayata.e_commerce2.model.CartItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrdersAdapter(private val orders: List<Pair<String, List<CartItem>>>) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {

//        fun bind(order: Pair<String, List<CartItem>>) {
//            // Sipariş tarihini ve ürünleri bağlama
//            val date = order.first
//            val items = order.second
//
//            binding.textViewOrderDate.text = "Sipariş Tarihi: $date"
//
//            // Ürünleri birden fazla cardview içine yerleştirme
//            items.forEach { item ->
//                // Burada, her bir ürün için ayrı bir CardView oluşturabilirsiniz
//                binding.textViewProductName.text = item.title
//                binding.textViewProductQuantity.text = "Quantity: ${item.count}"
//                binding.textViewProductPrice.text = "Price: ${item.price} TL"
//
//                Glide.with(binding.imageViewProduct.context)
//                    .load(item.image)
//                    .into(binding.imageViewProduct)
//            }
//        }
        fun bind(order: Pair<String, List<CartItem>>) {
            // Sipariş tarihini ve ürünleri bağlama
            val date = order.first
            val items = order.second

            // Tarihi doğru formatta gösterme
            val timestamp = date.toLong()
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(timestamp))


            // Sipariş tarihini TextView'a ekleme
            binding.textViewOrderDate.text = "Sipariş Tarihi: $formattedDate"

            val totalPrice = items.sumOf { (it.price ?: 0.0) * (it.count ?: 0) }
            binding.textViewTotalPrice.text = "Toplam Ücret: ${String.format("%.2f", totalPrice)} TL"

            // Ürünleri sırasıyla eklemek için:
            // Birden fazla ürün olduğu için LinearLayout veya benzer bir yapı ile her ürün için alt bir görünüm ekleyeceğiz.

            // Burada her siparişin ürünlerini ayrı bir CardView içinde göstereceğiz.
            // Ürünleri görsel olarak eklemek için bir layout yeniden kullanacağız (bunu LayoutInflater ile yapabiliriz)

            // Dinamik olarak ürünleri ekleme:
            items.forEachIndexed { index, item ->
                val productView = LayoutInflater.from(binding.root.context)
                    .inflate(R.layout.item_product, null)

                // Dinamik ürün verilerini bağlama
                val productName = productView.findViewById<TextView>(R.id.textViewProductName)
                val productQuantity = productView.findViewById<TextView>(R.id.textViewProductQuantity)
                val productPrice = productView.findViewById<TextView>(R.id.textViewProductPrice)
                val productImage = productView.findViewById<ImageView>(R.id.imageViewProduct)

                productName.text = item.title
                productQuantity.text = "Quantity: ${item.count}"
                productPrice.text = "Price: ${item.price} TL"

                // Glide ile ürün görselini ekleme
                Glide.with(binding.root.context)
                    .load(item.image)
                    .into(productImage)

                // Ürünü mevcut cardview'a ekliyoruz
                binding.productContainer.addView(productView) // `productContainer`, ürünlerin gösterileceği alan
            }
        }
    }
}
