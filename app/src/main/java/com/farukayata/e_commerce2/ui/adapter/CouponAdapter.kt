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
