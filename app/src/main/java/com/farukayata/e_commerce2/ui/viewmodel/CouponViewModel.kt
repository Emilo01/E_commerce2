package com.farukayata.e_commerce2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.data.repo.CouponRepository
import com.farukayata.e_commerce2.model.CartItem
import com.farukayata.e_commerce2.model.Coupon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CouponViewModel @Inject constructor(
    private val couponRepository: CouponRepository
) : ViewModel() {

    private val _coupons = MutableLiveData<List<Coupon>>()
    val coupons: LiveData<List<Coupon>> get() = _coupons

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> get() = _totalPrice

    val isCouponApplied = MutableLiveData<Boolean>() //Kupon durumu için LiveData eklendik

    // Sepet fiyatını günceller - cart fragmentta manuel yazdık
    fun updateTotalPrice(products: List<CartItem>, appliedCoupon: Coupon?) {
        val total = products.sumOf { (it.price ?: 0.0) * (it.count ?: 0) }
        val discount = appliedCoupon?.discountAmount ?: 0.0
        _totalPrice.value = (total - discount).coerceAtLeast(0.0) //Toplam fiyat negatif olamaz
    }

    // Kullanıcının kuponlarını getirir
    fun fetchUserCoupons() {
        viewModelScope.launch {
            val coupons = couponRepository.getUserCoupons()
            _coupons.value = coupons
        }
    }

    // Kuponu uygular
//    fun applyCoupon(couponCode: String, products: List<CartItem>) {
//        viewModelScope.launch {
//            val coupon = couponRepository.validateCoupon(couponCode)
//            coupon?.let {
//                // Kupon geçerli ise toplam fiyatı güncelle
//                updateTotalPrice(products, it)
//            }
//        }
//    }


    fun applyCoupon(couponCode: String, cartItems: List<CartItem>) {
        viewModelScope.launch {
            val coupon = couponRepository.validateCoupon(couponCode)
            if (coupon != null) {
                val total = cartItems.sumOf { (it.price ?: 0.0) * (it.count ?: 0) }
                val discount = coupon.discountAmount ?: 0.0
                _totalPrice.value = (total - discount).coerceAtLeast(0.0) //Fiyat sıfırın altına düşemez
                isCouponApplied.postValue(true) //Kupon başarıyla uygulandı
            } else {
                _totalPrice.value = cartItems.sumOf { (it.price ?: 0.0) * (it.count ?: 0) }
                isCouponApplied.postValue(false) //Geçersiz kupon
            }
        }
    }

    fun removeCoupon(cartItems: List<CartItem>) {
        val total = cartItems.sumOf { (it.price ?: 0.0) * (it.count ?: 0) }
        _totalPrice.postValue(total)
        isCouponApplied.postValue(false) //Kupon kaldırıldı
    }
}
