package com.farukayata.e_commerce2.ui.adapter //->coupo copy özellikli adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.farukayata.e_commerce2.databinding.ItemCouponBinding
import com.farukayata.e_commerce2.model.Coupon
import java.text.SimpleDateFormat
import java.util.*

class CouponAdapter(
    private val coupons: List<Coupon>,
    private val onCouponApplied: (Coupon) -> Unit //bu kısımı kaldırıp couponns ve profile fragmenttı güncelleye biliriz
) : RecyclerView.Adapter<CouponAdapter.CouponViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val binding = ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val coupon = coupons[position]
        holder.bind(coupon)
    }

    override fun getItemCount(): Int = coupons.size

    inner class CouponViewHolder(private val binding: ItemCouponBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coupon: Coupon) {
            val context = binding.root.context

            binding.textViewCouponCode.text = coupon.code ?: "Geçersiz Kupon"

            //kupon son kullama tarihi geçerliliği ve kontrılü
            coupon.issuedDate?.let { date ->
                val validityDate = calculateValidityDate(date)
                binding.textViewValidity.text = "Valid Until $validityDate"

                val isCouponValid = isCouponStillValid(date)
                binding.buttonCopyCoupon.isEnabled = isCouponValid
                binding.buttonCopyCoupon.alpha = if (isCouponValid) 1f else 0.5f
            } ?: run {
                binding.textViewValidity.text = "Geçerlilik tarihi bulunamadı"
                binding.buttonCopyCoupon.isEnabled = false
                binding.buttonCopyCoupon.alpha = 0.5f
            }

            binding.textViewDiscountAmount.text = "Kupon indirim tutarı ${coupon.discountAmount ?: 0} TL'dir"

            binding.buttonCopyCoupon.setOnClickListener {
                copyToClipboard(context, coupon.code.orEmpty())
            }
        }

        //kuponn kodunu kopyalama fonksiyou
        private fun copyToClipboard(context: Context, text: String) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Coupon Code", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Kupon Kopyalandı: $text", Toast.LENGTH_SHORT).show()
        }

        //kupon geçerlilik tarihi hesaplama
        private fun calculateValidityDate(issuedDate: Date): String {
            val calendar = Calendar.getInstance()
            calendar.time = issuedDate
            calendar.add(Calendar.DAY_OF_YEAR, 7)

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            return dateFormat.format(calendar.time)
        }

        //kupo son kullama tarihi doldu mu kontrolü için
        private fun isCouponStillValid(issuedDate: Date): Boolean {
            val calendar = Calendar.getInstance()
            calendar.time = issuedDate
            calendar.add(Calendar.DAY_OF_YEAR, 7)

            val expiryDate = calendar.time
            val currentDate = Date()

            return currentDate.before(expiryDate)
        }
    }
}








/* -> coupon apply özellikli adapter
package com.farukayata.e_commerce2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.farukayata.e_commerce2.databinding.ItemCouponBinding
import com.farukayata.e_commerce2.model.Coupon
import java.text.SimpleDateFormat
import java.util.*

class CouponAdapter(
    private val coupons: List<Coupon>,
    private val onCouponApplied: (Coupon) -> Unit
) : RecyclerView.Adapter<CouponAdapter.CouponViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val binding = ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val coupon = coupons[position]
        holder.bind(coupon)
    }

    override fun getItemCount(): Int = coupons.size

    inner class CouponViewHolder(private val binding: ItemCouponBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coupon: Coupon) {
            val context = binding.root.context

            // Kupon Kodu
            binding.textViewCouponCode.text = coupon.code ?: "Geçersiz Kupon"

            // Kupon Geçerlilik Tarihi ve Geçerlilik Kontrolü
            coupon.issuedDate?.let { date ->
                val validityDate = calculateValidityDate(date)
                binding.textViewValidity.text = "Valid Until $validityDate"

                val isCouponValid = isCouponStillValid(date)
                binding.buttonApplyCoupon.isEnabled = isCouponValid
                binding.buttonApplyCoupon.alpha = if (isCouponValid) 1f else 0.5f
            } ?: run {
                binding.textViewValidity.text = "Geçerlilik tarihi bulunamadı"
                binding.buttonApplyCoupon.isEnabled = false
                binding.buttonApplyCoupon.alpha = 0.5f
            }

            // Kupon Açıklaması
            binding.textViewDiscountAmount.text = "Kupon indirim tutarı ${coupon.discountAmount ?: 0} TL'dir"

            // Apply Butonuna Tıklama Olayı
            binding.buttonApplyCoupon.setOnClickListener {
                if (coupon.issuedDate != null && isCouponStillValid(coupon.issuedDate)) {
                    onCouponApplied(coupon)
                } else {
                    Toast.makeText(context, "Bu kuponun süresi dolmuştur", Toast.LENGTH_SHORT).show()
                }
            }
        }


        //Kuponun kazanıldığı tarihten 7 gün sonrası hesabı
        private fun calculateValidityDate(issuedDate: Date): String {
            val calendar = Calendar.getInstance()
            calendar.time = issuedDate
            calendar.add(Calendar.DAY_OF_YEAR, 7)

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            return dateFormat.format(calendar.time)
        }

       //kupon kullanım tarihi dolup dolmadığı kısım
        private fun isCouponStillValid(issuedDate: Date): Boolean {
            val calendar = Calendar.getInstance()
            calendar.time = issuedDate
            calendar.add(Calendar.DAY_OF_YEAR, 7)

            val expiryDate = calendar.time
            val currentDate = Date()

            return currentDate.before(expiryDate)
        }
    }
}



 */








/*
package com.farukayata.e_commerce2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.farukayata.e_commerce2.databinding.ItemCouponBinding
import com.farukayata.e_commerce2.model.Coupon

class CouponAdapter(
    private val coupons: List<Coupon>,
    private val onCouponApplied: (Coupon) -> Unit
) : RecyclerView.Adapter<CouponAdapter.CouponViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val binding = ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val coupon = coupons[position]
        holder.bind(coupon)
    }

    override fun getItemCount(): Int = coupons.size

    inner class CouponViewHolder(private val binding: ItemCouponBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coupon: Coupon) {
            // Null kontrolü ekleyerek kupon kodunu ve indirim miktarını gösterecek şekilde düzenleme
            binding.textViewCouponCode.text = coupon.code ?: "Geçersiz Kupon" // Null ise 'Geçersiz Kupon' yazdır
            binding.textViewDiscountAmount.text = "İndirim: ${coupon.discountAmount?.let { "$it TL" } ?: "0 TL"}" // Null ise 0 TL göster

            // Uygula butonuna tıklanması durumunda kupon uygulama işlemi
            binding.buttonApplyCoupon.setOnClickListener {
                if (coupon.code != null && coupon.discountAmount != null) {
                    onCouponApplied(coupon) // Kuponu uygulama işlemi
                } else {
                    // Geçersiz veya eksik kupon durumu için mesaj gösterilebilir.
                    Toast.makeText(binding.root.context, "Geçersiz Kupon", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}


 */