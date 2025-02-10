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
            // Ürün Bilgileri (Görsel, Ad, Adet, Fiyat)
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
        val (order, item) = expandedOrderList[position] // Her siparişten gelen ürünü al
        //burda da gereksiz pair kullanımı var order kullanmadığımız içim
        holder.bind(order, item)
    }

    override fun getItemCount(): Int {
        Log.d("ReceiptAdapter", "Adapter içindeki ürün sayısı: ${expandedOrderList.size}")
        return expandedOrderList.size
    }
}






/*
package com.farukayata.e_commerce2.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.databinding.ItemReceiptBinding
import com.farukayata.e_commerce2.model.CartItem
import com.farukayata.e_commerce2.model.Order
import java.text.SimpleDateFormat
import java.util.*

class ReceiptAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder>() {

    // **Her siparişteki ürünleri ayrı bir listeye dönüştürüyoruz**
//    private val expandedOrderList = orders.flatMap { order ->
//        order.items.map { item -> Pair(order, item) }
//    }
    private val expandedOrderList = orders.flatMap { it.items }

    class ReceiptViewHolder(private val binding: ItemReceiptBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            //bu kısımlar artık fragment receipt içinnde olcak
//            binding.textViewOrderDate.text = "Sipariş Tarihi: ${formatTimestamp(order.timestamp)}"
//            binding.textViewOrderTotal.text = "Toplam: ${order.totalAmount} TL"
//            binding.textViewOrderId.text = "Sipariş ID: ${order.id}"

            // Ürün Bilgileri (Resim, Ad, Adet, Fiyat)
            Glide.with(binding.imageViewProduct.context)
                .load(item.image)  // Firestore'dan gelen resim URL'si
                .into(binding.imageViewProduct)

            binding.textViewProductName.text = item.title ?: "Ürün Adı yok"
            binding.textViewProductQuantity.text = "Adet: ${item.count ?: 1}"
            binding.textViewProductPrice.text = "Fiyat: ${item.price ?: 0.0} TL"
        }

        // Timestamp değerini tarih formatına çevirme---bunada burda gerek kalmadı fragmentta gidicek
//        private fun formatTimestamp(timestamp: Long): String {
//            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) // Gün/Ay/Yıl Saat:Dakika formatı
//            return sdf.format(Date(timestamp))
//        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val binding = ItemReceiptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReceiptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        //val (_, item) = expandedOrderList[position] //Order kullanılmadığı için atlandı "_" kullanarak
        val item = expandedOrderList[position] //order'ı kaldırdık
        holder.bind(item)  // Order ve item birlikte gönderildi
    }

    //override fun getItemCount(): Int = orders.size -- log attık
    override fun getItemCount(): Int {
        Log.d("ReceiptAdapter", " Adapter içindeki sipariş sayısı: ${orders.size}")
        //return orders.size -- artık cart yapısınndan verileri alıyoruz itemxml içinn o yüzden bunnu ignor ettik
        return expandedOrderList.size
    }

}

 */
