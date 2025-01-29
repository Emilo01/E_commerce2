package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farukayata.e_commerce2.databinding.FragmentCartBinding
import com.farukayata.e_commerce2.model.CartItem
import com.farukayata.e_commerce2.ui.adapter.CartAdapter
import com.farukayata.e_commerce2.ui.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

//nullable olabilirlik durumu için

@AndroidEntryPoint
class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private val viewModel: CartViewModel by viewModels()
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        // Adapter'ı oluşturma ve lambda fonksiyonları tanımlama
        adapter = CartAdapter(
            onRemoveClick = { productId ->
                viewModel.removeFromCart(productId) // Ürünü sepetten kaldır
            },
            onIncreaseClick = { cartItem ->
                val newCount = (cartItem.count ?: 0) + 1 // Null kontrolü yapıldı
                viewModel.updateItemCount(cartItem.id.orEmpty(), newCount) // Adeti artır
            },
            onDecreaseClick = { cartItem ->
                val newCount = (cartItem.count ?: 0) - 1 // Null kontrolü yapıldı
                if (newCount > 0) {
                    viewModel.updateItemCount(cartItem.id.orEmpty(), newCount)
                // Adeti azaltır
                } else {
                    viewModel.removeFromCart(cartItem.id.orEmpty())
                // Adet 0 sa ürünü siler
                }
            }
        )

        // RecyclerView ayarları
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCart.adapter = adapter

        // Sepet verilerini gözlemle ve RecyclerView'a bağla
        viewModel.cartItems.observe(viewLifecycleOwner) { cartList ->
            adapter.submitList(cartList) // Adapter'e yeni listeyi ilet
            updateTotalPrice(cartList) // Toplam fiyatı güncelle
        }


        // ItemTouchHelper ile kartı sağdan sola kaydırarak silme özelliği
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            //Kaydırma İşlemi: onSwiped ile kaydırılan öğe Firestore'dan ve UI'dan kaldırılıyor
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true // Taşıma işlemi yok
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val cartItem = adapter.currentList[position]
                viewModel.removeFromCart(cartItem.id.orEmpty()) // Kaydırılan ürünü sil
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewCart)

        return binding.root
    }

    // Toplam fiyatı güncelleme fonksiyonu
    private fun updateTotalPrice(cartList: List<CartItem>) {
        val totalPrice = cartList.sumOf { (it.price ?: 0.0) * (it.count ?: 0) } // Null kontrolü eklendi
        binding.textViewTotalPrice.text = String.format("Total: %.2f TL", totalPrice)
    }
}











//null able olmaya durum için
/*
package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farukayata.e_commerce2.databinding.FragmentCartBinding
import com.farukayata.e_commerce2.model.CartItem
import com.farukayata.e_commerce2.ui.adapter.CartAdapter
import com.farukayata.e_commerce2.ui.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private val viewModel: CartViewModel by viewModels()
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        // Adapter'ı oluşturma ve lambda fonksiyonları tanımlama
        adapter = CartAdapter(
            onRemoveClick = { productId ->
                viewModel.removeFromCart(productId) // Ürünü sepetten kaldır
            },
            onIncreaseClick = { cartItem ->
                viewModel.updateItemCount(cartItem.id.toString(), cartItem.count + 1) // Adeti artır
            },
            onDecreaseClick = { cartItem ->
                viewModel.updateItemCount(cartItem.id.toString(), cartItem.count - 1) // Adeti azalt
            }
        )

        // RecyclerView ayarları
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCart.adapter = adapter

        // Sepet verilerini gözlemle ve RecyclerView'a bağla
        viewModel.cartItems.observe(viewLifecycleOwner) { cartList ->
            adapter.submitList(cartList) // Adapter'e yeni listeyi ilet
            updateTotalPrice(cartList) // Toplam fiyatı güncelle
        }

        // ItemTouchHelper ile kartı sağdan sola kaydırarak silme özelliği
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // Taşıma işlemi yok
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val cartItem = adapter.currentList[position]
                viewModel.removeFromCart(cartItem.id.toString()) // Kaydırılan ürünü sil
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewCart)

        return binding.root
    }

    // Toplam fiyatı güncelleme fonksiyonu
    private fun updateTotalPrice(cartList: List<CartItem>) {
        val totalPrice = cartList.sumOf { it.price * it.count }
        binding.textViewTotalPrice.text = String.format("Total: %.2f TL", totalPrice)
    }
}


 */