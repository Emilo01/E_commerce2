package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.farukayata.e_commerce2.databinding.FragmentReceiptBinding
import com.farukayata.e_commerce2.model.Order
import com.farukayata.e_commerce2.ui.adapter.ReceiptAdapter
import com.farukayata.e_commerce2.ui.util.EmailSender
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
    private val emailSender = EmailSender()

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

            // E-posta gönderme işlemi
            sendReceiptEmail(order)
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

    private fun sendReceiptEmail(order: Order) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) return

        val userEmail = currentUser.email ?: return
        val userName = "${currentUser.displayName ?: "Müşteri"}" //-> emailsenderdaki customerName burdan gitcek
        val orderDate = formatTimestamp(order.timestamp)
        val totalPrice = "${order.totalAmount} TL"

        // Ürün detaylarını hazırla
        val itemDetails = order.items.map { item ->
            "${item.title ?: "Ürün"} - ${item.price ?: 0.0} TL x ${item.count ?: 1}"
        }

        emailSender.sendReceiptEmail(
            recipientEmail = userEmail,
            customerName = userName,
            orderDate = orderDate,
            totalPrice = totalPrice,
            items = itemDetails,
            onSuccess = {
                Toast.makeText(context, "Sipariş fişi e-posta adresinize gönderildi.", Toast.LENGTH_LONG).show()
            },
            onError = { errorMessage ->
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        )
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

 */