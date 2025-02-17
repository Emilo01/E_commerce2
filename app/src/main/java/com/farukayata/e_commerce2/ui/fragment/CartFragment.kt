package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentCartBinding
import com.farukayata.e_commerce2.model.CartItem
import com.farukayata.e_commerce2.ui.adapter.CartAdapter
import com.farukayata.e_commerce2.ui.adapter.CouponAdapter
import com.farukayata.e_commerce2.ui.viewmodel.CartViewModel
import com.farukayata.e_commerce2.ui.viewmodel.CouponViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

//nullable olabilirlik durumu için

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private lateinit var binding: FragmentCartBinding
    private val viewModel: CartViewModel by viewModels()
    private lateinit var adapter: CartAdapter
    private var discountPrice: Double? = null
    private var finalPrice: Double? = null

    private lateinit var couponAdapter: CouponAdapter //recycler view kullanmıcaz duruma göre kaldırıla bilir
    private val couponViewModel: CouponViewModel by viewModels()

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
                val newCount = (cartItem.count ?: 0) - 1 // Null kontrolü yapıldı
                if (newCount > 0) {
                    viewModel.updateItemCount(cartItem.id.orEmpty(), newCount)
                    // Adeti azaltır
                } else {
                    viewModel.removeFromCart(cartItem.id.orEmpty())
                    // Adet 0 sa ürünü siler
                }


            },
            onDecreaseClick = { cartItem ->
                val newCount = (cartItem.count ?: 0) + 1 // Null kontrolü yapıldı
                viewModel.updateItemCount(cartItem.id.orEmpty(), newCount) // Adeti artır
            }
        )

        // RecyclerView ayarları
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCart.adapter = adapter

        //burayı iptal ettik çünkü ödeme ekranında recycler view kullamaktan vaz geçtik
//        // Kuponlar için adapter
//        couponAdapter = CouponAdapter(coupons = listOf()) { coupon ->
//            couponViewModel.applyCoupon(coupon.code, viewModel.cartItems.value ?: listOf())
//        }
//        binding.recyclerViewCoupons.layoutManager = LinearLayoutManager(requireContext())
//        binding.recyclerViewCoupons.adapter = couponAdapter
//
//        // Kuponları al
//        couponViewModel.fetchUserCoupons()
//
//        couponViewModel.coupons.observe(viewLifecycleOwner) { coupons ->
//            couponAdapter.notifyDataSetChanged()
//        }


        //lotie ekledik ve sepet boş olma ve olmama durumu olucağı için artık aşağıdaki gibi değilde bi aşağıdaki gibi kullancaz
        // Sepet verilerini gözlemle ve RecyclerView'a bağla
//        viewModel.cartItems.observe(viewLifecycleOwner) { cartList ->
//            adapter.submitList(cartList) // Adapter'e yeni listeyi ilet
//            updateTotalPrice(cartList) // Toplam fiyatı güncelle
//        }

        viewModel.cartItems.observe(viewLifecycleOwner) { cartList ->
            if (cartList.isEmpty()) {
                // Eğer sepet boşsa ürünleri gizle ve boş sepet mesajını gösterir
                binding.recyclerViewCart.visibility = View.GONE
                binding.emptyCartLayout.visibility = View.VISIBLE
            } else {
                // Eğer sepet doluysa listeyi göster ve boş sepet mesajını gizler
                binding.recyclerViewCart.visibility = View.VISIBLE
                binding.emptyCartLayout.visibility = View.GONE
            }
            updateTotalPrice(cartList)
        }

        binding.buttonShopNow.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_homeFragment)
        }

        //Satın Al Butonu - PaymentSelectionFragment'a yönlendirme
        binding.btnCheckout.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_paymentSelectionFragment)
        }

        // Kupon Uygula Butonu
        binding.buttonApplyCoupon.setOnClickListener {
            val couponCode = binding.editTextCouponCode.text.toString().trim()
            if (couponCode.isNotEmpty()) {
                couponViewModel.applyCoupon(couponCode, viewModel.cartItems.value ?: listOf())
            } else {
                // Kullanıcı kupon kodunu girmediyse bir uyarı gösterebiliriz.
                Toast.makeText(context, "Kupon kodunu girin", Toast.LENGTH_SHORT).show()
            }
            //gereksiz ve sıkıtılı olabilir !!!!!!!
            couponViewModel.totalPrice.observe(viewLifecycleOwner) { discountedPrice ->
                binding.textViewTotalPrice.text = String.format("Toplam: %.2f TL", discountedPrice)
            }
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

    override fun onDestroyView() {
        //Memory Leak Önleme: Fragment kapandığında UI referanslarını serbest bırakır.
        super.onDestroyView()
        _binding = null
    }

    // Toplam fiyatı güncelleme fonksiyonu
    private fun updateTotalPrice(cartList: List<CartItem>) {
        val totalPrice = cartList.sumOf { (it.price ?: 0.0) * (it.count ?: 0) } // Null kontrolü eklendi
        couponViewModel.totalPrice.observe(viewLifecycleOwner) { discounttPrice ->
            discountPrice = discounttPrice
        }

        discountPrice?.let { discount ->
            finalPrice = totalPrice - discount
        } ?: run {
            finalPrice = totalPrice
        }
        binding.textViewTotalPrice.text = String.format("Toplam: %.2f TL", finalPrice)
        /*val finalPrice = discountPrice ?: totalPrice
            binding.textViewTotalPrice.text = String.format("Toplam: %.2f TL", finalPrice)*/

        //şu kısımı ekledik çünkü sepet boş sayfası özelliği eklediğmizde
        //sepete eklene ürünler sepet sayfamızda görülmüyordu
        //sepete ürün eklenince recycler view güncelleniyor kısaca
        adapter.submitList(cartList)
        adapter.notifyDataSetChanged()
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