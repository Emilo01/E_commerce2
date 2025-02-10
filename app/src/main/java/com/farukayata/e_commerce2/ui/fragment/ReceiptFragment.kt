package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.farukayata.e_commerce2.databinding.FragmentReceiptBinding
import com.farukayata.e_commerce2.model.Order
import com.farukayata.e_commerce2.ui.adapter.ReceiptAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ReceiptFragment : Fragment() {

    private lateinit var binding: FragmentReceiptBinding
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var latestOrder: Order? = null //Yalnızca en son siparişi göstermek için kullanıcaz

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReceiptBinding.inflate(inflater, container, false)

        // RecyclerView ayarları
        binding.recyclerViewReceipt.layoutManager = LinearLayoutManager(requireContext())

        //yalız son aldığımız ürünün fişi için
        latestOrder = arguments?.getParcelable("latest_order")

        latestOrder?.let { order ->
            //parcelable sayesinde fragmentlar arası veri taşıdık
            binding.tvOrderDate.text = "Sipariş Tarihi: ${formatTimestamp(order.timestamp)}"
            binding.tvTotalPrice.text = "Toplam Fiyat: ${order.totalAmount} TL"
            binding.recyclerViewReceipt.adapter = ReceiptAdapter(listOf(order))
        //Sadece latestOrder gönderildi--boş bir listede ekledik
        } ?: run {
            //latestOrder null ise -- heüz siparişiniz yok yazızı gösterilcek
            binding.tvNoOrders.visibility = View.VISIBLE
        }

        // Kullanıcı bilgilerini ekrana yazdık
        loadUserInfo()

        return binding.root
    }

    private fun loadUserInfo() {
        val currentUser = firebaseAuth.currentUser
        binding.tvCustomerEmail.text = "Email: ${currentUser?.email}"

        currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val firstName = document.getString("firstName") ?: ""
                    val lastName = document.getString("lastName") ?: ""
                    binding.tvCustomerName.text = "Müşteri: $firstName $lastName"
                }
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}



/*
package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.farukayata.e_commerce2.databinding.FragmentReceiptBinding
import com.farukayata.e_commerce2.ui.adapter.ReceiptAdapter
import com.farukayata.e_commerce2.ui.viewmodel.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReceiptFragment : Fragment() {

    private lateinit var binding: FragmentReceiptBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReceiptBinding.inflate(inflater, container, false)

        // RecyclerView ayarları
        binding.recyclerViewReceipt.layoutManager = LinearLayoutManager(requireContext())

        // Kullanıcı bilgilerini al ve ekrana yaz
        loadUserInfo()

        // Firestore'dan siparişleri çek ve RecyclerView'a ekle
        orderViewModel.orders.observe(viewLifecycleOwner) { orders ->
            Log.d("ReceiptFragment", "Firestore'dan gelen sipariş sayısı: ${orders.size}")

            if (orders.isNotEmpty()) {
                binding.tvNoOrders.visibility = View.GONE
                binding.tvOrderDate.text = "Sipariş Tarihi: ${formatTimestamp(orders.first().timestamp)}"
                binding.tvTotalPrice.text = "Toplam Fiyat: ${orders.sumOf { it.totalAmount }} TL"

                val adapter = ReceiptAdapter(orders)
                binding.recyclerViewReceipt.adapter = adapter
            } else {
                binding.recyclerViewReceipt.visibility = View.VISIBLE
            }
        }

        orderViewModel.fetchOrders() // Firestore’dan siparişleri çek

        return binding.root
    }

    private fun loadUserInfo() {
        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val firstName = document.getString("firstName") ?: ""
                        val lastName = document.getString("lastName") ?: ""
                        binding.tvCustomerName.text = "Müşteri: $firstName $lastName"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ReceiptFragment", " Kullanıcı bilgileri alınamadı: ${e.localizedMessage}")
                }

            binding.tvCustomerEmail.text = "Email: ${currentUser.email}"
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}



 */





/*
package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.farukayata.e_commerce2.databinding.FragmentReceiptBinding
import com.farukayata.e_commerce2.ui.adapter.ReceiptAdapter
import com.farukayata.e_commerce2.ui.viewmodel.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReceiptFragment : Fragment() {

    private lateinit var binding: FragmentReceiptBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReceiptBinding.inflate(inflater, container, false)

        // RecyclerView ayarları
        binding.recyclerViewReceipt.layoutManager = LinearLayoutManager(requireContext())

        // Kullanıcı bilgilerini al ve ekrana yaz
        loadUserInfo()

        // Firestore'dan siparişleri çek ve RecyclerView'a ekle
        orderViewModel.orders.observe(viewLifecycleOwner) { orders ->
            Log.d("ReceiptFragment", " Firestore'dan gelen sipariş sayısı: ${orders.size}")

            if (orders.isNotEmpty()) {
                Log.d("ReceiptFragment", "Firestore'dan sipariş çekilemedi")
                //false dönnüyor sanırım adapter bağlanmadı hatası var çünkü
                //val adapter = ReceiptAdapter(orders) aşağıdaki gibi revize ettik
                binding.tvNoOrders.visibility = View.GONE  // Eğer sipariş varsa mesajı gizle
                val adapter = ReceiptAdapter(orders.flatMap { it.items }) // 🔥 Sepetteki her ürünü ayrı göster
                binding.recyclerViewReceipt.adapter = adapter
            } else {
                binding.recyclerViewReceipt.visibility = View.VISIBLE
            }
        }

        orderViewModel.fetchOrders() // Firestore’dan siparişleri çek

        return binding.root
    }

    /** Kullanıcının adı ve e-posta bilgilerini al ve ekrana yaz */
    private fun loadUserInfo() {
        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val firstName = document.getString("firstName") ?: ""
                        val lastName = document.getString("lastName") ?: ""
                        binding.tvCustomerName.text = "Müşteri: $firstName $lastName"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ReceiptFragment", "❌ Kullanıcı bilgileri alınamadı: ${e.localizedMessage}")
                }

            // Kullanıcının e-posta adresini Firebase Authentication'dan çek
            binding.tvCustomerEmail.text = "Email: ${currentUser.email}"
        }
    }

}


 */






/*
package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.farukayata.e_commerce2.databinding.FragmentReceiptBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReceiptFragment : Fragment() {

    private var _binding: FragmentReceiptBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReceiptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


 */