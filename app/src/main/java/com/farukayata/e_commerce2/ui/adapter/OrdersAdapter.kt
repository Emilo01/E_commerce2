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

class OrdersAdapter(
    private val orders: List<Pair<String, List<CartItem>>>,
    private val onProductClick: (CartItem) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

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

        fun bind(order: Pair<String, List<CartItem>>) {
            //sipraiş tarihi ile ürünleri bağladık
            val date = order.first
            val items = order.second

            //tarih formatı çevirdik
            val timestamp = date.toLong()
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(timestamp))


            //sipraiş tarihini ekledik text te
            binding.textViewOrderDate.text = "Sipariş Tarihi: $formattedDate"

            val totalPrice = items.sumOf { (it.price ?: 0.0) * (it.count ?: 0) }
            binding.textViewTotalPrice.text = "Toplam Ücret: ${String.format("%.2f", totalPrice)} TL"

            // ürünleri sırasıyla eklemek için:
            // birden fazla ürün olduğu için LinearLayout veya benzer bir yapı ile her ürün için alt bir görünüm ekleyeceğiz

            // burada her siparişin ürünlerini ayrı bir CardView içinde göstereceğiz
            // ürünleri görsel olarak eklemek için bir layout yeniden kullanacağız (bunu LayoutInflater ile yapabiliriz)

            binding.productContainer.removeAllViews()
            //bunu eklemeden önce scrool yaptığıızda ürünler card içinde karışıyor du

            // dinamik olarak ürünleri ekleme:
            items.forEachIndexed { index, item ->
                val productView = LayoutInflater.from(binding.root.context)
                    .inflate(R.layout.item_product, binding.productContainer, false)

                /*
                - yukarıdaki hali ile gereksiz hesaplamaları kaldırdık şimdi hangi viewgrouppa bağlaacağını biliyor
                - Burada null olarak bırakman, layoutun hangi parent içine ekleneceğini belirtmediğin anlamına gelir
                val productView = LayoutInflater.from(binding.root.context)
                    .inflate(R.layout.item_product, null)
                 */

                //ürün verilerini şimdilik dinamik bağladım
                val productName = productView.findViewById<TextView>(R.id.textViewProductName)
                val productQuantity = productView.findViewById<TextView>(R.id.textViewProductQuantity)
                val productPrice = productView.findViewById<TextView>(R.id.textViewProductPrice)
                val productImage = productView.findViewById<ImageView>(R.id.imageViewProduct)

                productName.text = item.title
                productQuantity.text = "Quantity: ${item.count}"
                productPrice.text = "Price: ${item.price} TL"

                //ürü görseli ekledik
                Glide.with(binding.root.context)
                    .load(item.image)
                    .into(productImage)

                // ürün cart a tıklannabilirlik ekledik
                productView.setOnClickListener {
                    onProductClick(item)
                }

                binding.productContainer.addView(productView)
            }
        }
    }
}