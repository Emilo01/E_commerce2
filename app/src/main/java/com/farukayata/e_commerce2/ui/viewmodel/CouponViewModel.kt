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
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CouponViewModel @Inject constructor(
    private val couponRepository: CouponRepository
) : ViewModel() {

    private val _coupons = MutableLiveData<List<Coupon>>()
    val coupons: LiveData<List<Coupon>> get() = _coupons

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> get() = _totalPrice

    val isCouponApplied = MutableLiveData<Boolean>()
    val isCouponValid = MutableLiveData<Boolean>()

    init {
        fetchUserCoupons()
    }

    // Kullanıcının kuponlarını getirir ve süresi geçenleri siler
    fun fetchUserCoupons() {
        viewModelScope.launch {
            val allCoupons = couponRepository.getUserCoupons()
            val validCoupons = allCoupons.filter { isCouponStillValid(it.issuedDate) }
            _coupons.value = validCoupons
        }
    }

    // Kuponun süresinin dolup dolmadığını kontrol eder
    // Kuponun süresini check etti
    fun isCouponStillValid(issuedDate: Date?): Boolean {
        if (issuedDate == null) return false

        val calendar = Calendar.getInstance()
        calendar.time = issuedDate
        calendar.add(Calendar.DAY_OF_YEAR, 7) // Kuponun 7 gün süresi var

        val expiryDate = calendar.time
        val currentDate = Date()

        return currentDate.before(expiryDate)
        //son kullanma tarihi ile bugünün tarihii, compare ediyoruz
    }


    fun applyCoupon(couponCode: String, cartItems: List<CartItem>) {
        viewModelScope.launch {
            val coupon = couponRepository.validateCoupon(couponCode)
            val updatedCartItems = cartItems

            if (coupon != null) {
                val total = updatedCartItems.sumOf { (it.price ?: 0.0) * (it.count ?: 0) }
                val discount = coupon.discountAmount ?: 0.0

                _totalPrice.postValue((total - discount).coerceAtLeast(0.0))
                isCouponApplied.postValue(true)
            } else {
                _totalPrice.postValue(updatedCartItems.sumOf { (it.price ?: 0.0) * (it.count ?: 0) })
                isCouponApplied.postValue(false)
            }
        }
    }

    fun removeCoupon(cartItems: List<CartItem>) {
        val total = cartItems.sumOf { (it.price ?: 0.0) * (it.count ?: 0) }

        _totalPrice.postValue(total) //post value kullaıp uı ımızı anınnda yansıttık
        isCouponApplied.postValue(false)
    }
    fun validateCoupon(couponCode: String) {
        viewModelScope.launch {
            val coupon = couponRepository.validateCoupon(couponCode)
            isCouponValid.postValue(coupon != null)
            //eğer kupon varsa true dödürcek yoksa false döndürecek
        }
    }
}



/*
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

    val isCouponApplied = MutableLiveData<Boolean>() //Kupon durumu için livedate eklendik
    val isCouponValid = MutableLiveData<Boolean>() // Kuponun geçerli olup olmadığını kontrol eden livedatamız


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
            val updatedCartItems = cartItems

            if (coupon != null) {
                val total = updatedCartItems.sumOf { (it.price ?: 0.0) * (it.count ?: 0) }
                val discount = coupon.discountAmount ?: 0.0

                _totalPrice.postValue((total - discount).coerceAtLeast(0.0))
                isCouponApplied.postValue(true)

                //Eski fiyatı hemen göstermek için güncelleme çağrısı ekleniyor
                _totalPrice.value = (total - discount).coerceAtLeast(0.0)
            } else {
                _totalPrice.postValue(updatedCartItems.sumOf { (it.price ?: 0.0) * (it.count ?: 0) })
                isCouponApplied.postValue(false)
            }
        }
    }


    fun validateCoupon(couponCode: String) {
        viewModelScope.launch {
            val coupon = couponRepository.validateCoupon(couponCode)
            isCouponValid.postValue(coupon != null)
        //eğer kupon varsa true dödürcek yoksa false döndürecek
        }
    }

    fun removeCoupon(cartItems: List<CartItem>) {
        val total = cartItems.sumOf { (it.price ?: 0.0) * (it.count ?: 0) }

        _totalPrice.postValue(total) //post value kullaıp uı ımızı anınnda yansıttık değişikliği
        isCouponApplied.postValue(false)
    }

}


 */