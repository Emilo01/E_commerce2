package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.farukayata.e_commerce2.databinding.FragmentOrdersBinding
import com.farukayata.e_commerce2.model.CartItem
import com.farukayata.e_commerce2.model.Order
import com.farukayata.e_commerce2.ui.adapter.OrdersAdapter
import com.farukayata.e_commerce2.ui.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private val orderViewModel: OrderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)

        orderViewModel.orders.observe(viewLifecycleOwner) { orders ->
            Log.d("OrdersFragment", "Received orders: $orders")
            if (orders.isNullOrEmpty()) {
                Log.d("OrdersFragment", "No orders found")
                // Eğer sipariş yoksa kullanıcıya bir Toast mesajı gösteriyoruz
                Toast.makeText(requireContext(), "Siparişiniz bulunmamaktadır", Toast.LENGTH_SHORT).show()
            } else {

                // Siparişleri tarihe göre grupla
                val groupedOrders = groupOrdersByDate(orders)

                Log.d("OrdersFragment", "Displaying orders")
                // Eğer sipariş varsa, RecyclerView'a verileri bağlıyoruz
                val adapter = OrdersAdapter(groupedOrders)
                binding.recyclerViewOrders.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerViewOrders.adapter = adapter
            }
        }

        //viewmodeldeki fonksiyonla üsiparişleri çekiyoruz
        orderViewModel.fetchOrders()

        return binding.root
    }

    // Siparişleri tarihe göre gruplayan fonksiyon
    private fun groupOrdersByDate(orders: List<Order>): List<Pair<String, List<CartItem>>> {
        val dateMap = mutableMapOf<String, MutableList<CartItem>>()

        // Her siparişi tarih bazında gruplama
        orders.forEach { order ->
            val date = order.timestamp.toString() // Tarih bilgisini kullan
            if (!dateMap.containsKey(date)) {
                dateMap[date] = mutableListOf()
            }
            // Siparişteki ürünleri bu tarihe ekle
            dateMap[date]?.addAll(order.items)
        }

        // Gruplanan siparişleri döndür
        return dateMap.entries.map { entry -> entry.key to entry.value }
    }

}
