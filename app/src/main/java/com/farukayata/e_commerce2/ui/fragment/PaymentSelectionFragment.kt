package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentPaymentSelectionBinding
import com.farukayata.e_commerce2.model.Order
import com.farukayata.e_commerce2.ui.adapter.PaymentCartAdapter
import com.farukayata.e_commerce2.ui.viewmodel.CartViewModel
import com.farukayata.e_commerce2.ui.viewmodel.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PaymentSelectionFragment : Fragment() {

    private var _binding: FragmentPaymentSelectionBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by viewModels()
    private val orderViewModel: OrderViewModel by viewModels()
    private var selectedPaymentMethod: String? = null
    private val TAG = "PaymentSelectionFragment"
    private var selectedCashOption: String? = null
    private lateinit var paymentCartAdapter: PaymentCartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //RecyclerView sepetteki ürünler
        binding.recyclerViewCartItems.layoutManager = LinearLayoutManager(requireContext())

        cartViewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            if (cartItems.isNotEmpty()) {
                binding.textViewCartItemsTitle.visibility = View.VISIBLE
                binding.recyclerViewCartItems.visibility = View.VISIBLE

                paymentCartAdapter = PaymentCartAdapter(cartItems)
                binding.recyclerViewCartItems.adapter = paymentCartAdapter
            } else {
                binding.textViewCartItemsTitle.visibility = View.GONE
                binding.recyclerViewCartItems.visibility = View.GONE
            }
        }

        //ödeme yöntemi kısımı
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_cash -> {
                    selectedPaymentMethod = "Nakit"
                    binding.radioGroupCashOptions.visibility = View.VISIBLE
                //alt seçenekleri göster (kapıda ödeme seçeekleri için)
                }
                R.id.radio_credit_card -> {
                    selectedPaymentMethod = "Kredi Kartı"
                    binding.radioGroupCashOptions.visibility = View.GONE
                    selectedCashOption = null
                }
                else -> {
                    selectedPaymentMethod = null
                    binding.radioGroupCashOptions.visibility = View.GONE
                }
            }
        }

        //nakit Ödeme Seçenekleri
        binding.radioGroupCashOptions.setOnCheckedChangeListener { _, checkedId ->
            selectedCashOption = when (checkedId) {
                R.id.radio_cash_at_door -> "Kapıda Nakit Ödeme"
                R.id.radio_card_at_door -> "Kapıda Kredi Kartı ile Ödeme"
                else -> null
            }
        }

        //ödemeyi tamamla butonu
        binding.btnContinue.setOnClickListener {
            if (selectedPaymentMethod == null) {
                Toast.makeText(requireContext(), "Lütfen bir ödeme yöntemi seçin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedPaymentMethod == "Nakit" && selectedCashOption == null) {
                Toast.makeText(requireContext(), "Lütfen bir nakit ödeme yöntemi seçin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "Ödeme Yöntemi Seçildi: $selectedPaymentMethod - $selectedCashOption")

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val userId = currentUser.uid

                cartViewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
                    if (cartItems.isNotEmpty()) {
                        val totalAmount = cartItems.sumOf { (it.price ?: 0.0) * (it.count ?: 0) }

                        val order = Order(
                            id = UUID.randomUUID().toString(),
                            userId = userId,
                            items = cartItems,
                            totalAmount = totalAmount,
                            timestamp = System.currentTimeMillis()
                        )

                        Log.d(TAG, "Sipariş oluşturuldu: $order")
                        orderViewModel.onPaymentSuccess(order)
                        //firestore'a siparişi kaydet

                        val bundle = Bundle()
                        bundle.putParcelable("latest_order", order)

                        if (selectedPaymentMethod == "Kredi Kartı") {
                            findNavController().navigate(R.id.action_paymentSelectionFragment_to_cardPaymentFragment, bundle)
                        } else {
                            findNavController().navigate(R.id.action_paymentSelectionFragment_to_orderConfirmationFragment, bundle)
                        }
                    }
                }
            }
        }
    }
}









/*
package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentPaymentSelectionBinding
import com.farukayata.e_commerce2.model.Order
import com.farukayata.e_commerce2.ui.viewmodel.CartViewModel
import com.farukayata.e_commerce2.ui.viewmodel.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PaymentSelectionFragment : Fragment() {

    private var _binding: FragmentPaymentSelectionBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by viewModels()
    private val orderViewModel: OrderViewModel by viewModels()
    private var selectedPaymentMethod: String? = null
    private val TAG = "PaymentSelectionFragment"
    private var selectedCashOption: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ödeme yöntemi seçimi
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_cash -> {
                    selectedPaymentMethod = "Nakit"
                    binding.radioGroupCashOptions.visibility = View.VISIBLE
                //nakit seçilince alt seçenekleri göster
                }
                R.id.radio_credit_card -> {
                    selectedPaymentMethod = "Kredi Kartı"
                    binding.radioGroupCashOptions.visibility = View.GONE
                    //kredi kartı seçildiğinde gizle
                    selectedCashOption = null
                //önceki seçimi temizle
                }
                else -> {
                    selectedPaymentMethod = null
                    binding.radioGroupCashOptions.visibility = View.GONE
                }
            }
        }

        // Nakit ödeme alt seçenekleri
        binding.radioGroupCashOptions.setOnCheckedChangeListener { _, checkedId ->
            selectedCashOption = when (checkedId) {
                R.id.radio_cash_at_door -> "Kapıda Nakit Ödeme"
                R.id.radio_card_at_door -> "Kapıda Kredi Kartı ile Ödeme"
                else -> null
            }
        }

        // Devam Et butonuna basıldığında
        binding.btnContinue.setOnClickListener {
            if (selectedPaymentMethod == null) {
                Toast.makeText(requireContext(), "Lütfen bir ödeme yöntemi seçin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Eğer nakit ödeme seçildiyse, alt seçeneklerden biri seçilmeli
            if (selectedPaymentMethod == "Nakit" && selectedCashOption == null) {
                Toast.makeText(requireContext(), "Lütfen bir nakit ödeme yöntemi seçin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "Ödeme Yöntemi Seçildi: $selectedPaymentMethod - $selectedCashOption")

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val userId = currentUser.uid

                cartViewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
                    if (cartItems.isNotEmpty()) {
                        val totalAmount = cartItems.sumOf { (it.price ?: 0.0) * (it.count ?: 0) }

                        val order = Order(
                            id = UUID.randomUUID().toString(),
                            userId = userId,
                            items = cartItems,
                            totalAmount = totalAmount,
                            timestamp = System.currentTimeMillis()
                        )

                        Log.d(TAG, "Sipariş oluşturuldu: $order")
                        orderViewModel.onPaymentSuccess(order) // Firestore'a siparişi kaydet

                        val bundle = Bundle()
                        bundle.putParcelable("latest_order", order)

                        // Kredi Kartı seçildiyse ödeme ekranına git
                        if (selectedPaymentMethod == "Kredi Kartı") {
                            findNavController().navigate(R.id.action_paymentSelectionFragment_to_cardPaymentFragment, bundle)
                        } else {
                            // Nakit seçildiyse doğrudan sipariş onay ekranına git
                            findNavController().navigate(R.id.action_paymentSelectionFragment_to_orderConfirmationFragment, bundle)
                        }
                    }
                }
            }
        }
    }
}


 */