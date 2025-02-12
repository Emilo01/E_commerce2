package com.farukayata.e_commerce2.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukayata.e_commerce2.data.repo.CouponRepository
import com.farukayata.e_commerce2.data.repo.OrderRepository
import com.farukayata.e_commerce2.model.Coupon
import com.farukayata.e_commerce2.model.Order
//import com.yourpackage.name.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository
) : ViewModel() {

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> get() = _orders

    // Firebase’den siparişleri çeker ve LiveData’ya(_orders) aktarır.
    fun fetchOrders() {
        viewModelScope.launch {
            try {
                Log.d("OrderViewModel", "Firestore'dan siparişler çekiliyor...")
                _orders.value = orderRepository.getOrdersFromFirebase()
                checkAndAddCoupon()
                Log.d("OrderViewModel", "Fetched orders: ${_orders.value}")
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Siparişleri alırken hata oluştu: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }

    private suspend fun checkAndAddCoupon() {
        val orderCount = _orders.value?.size ?: 0
        if (orderCount >= 3 && orderCount % 3 == 0) {
            // 3 siparişten sonra kupon ekle
            val coupon = Coupon(
                code = "DISCOUNT20",
                discountAmount = 20.0,
                isValid = true
            )
            viewModelScope.launch {
                couponRepository.addCoupon(coupon)
                Log.d("OrderViewModel", "Kupon eklendi: DISCOUNT20")
            }
            //couponRepository.addCoupon(coupon) // Firebase'e kuponu kaydet
        }
    }

    // Yeni siparişi Firebase’e kaydeder.
    fun saveOrder(order: Order) {
        viewModelScope.launch {
            try {
                Log.d("OrderViewModel", "Sipariş kaydetme işlemi başlatıldı! Sipariş: $order")
                orderRepository.saveOrder(order)
                fetchOrders() // Siparişler güncellendiğinde yeniden yükle
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Sipariş kaydedilemedi: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }

    //log atarken eklendi işlevi şu an yok
    // Ödeme tamamlandığında siparişi kaydet
    fun onPaymentSuccess(order: Order) {
        Log.d("OrderViewModel", "Ödeme başarılı! Sipariş Firestore’a kaydedilecek...")
        saveOrder(order)
    }
}
