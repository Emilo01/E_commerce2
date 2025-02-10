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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedPaymentMethod = when (checkedId) {
                R.id.radio_cash -> "Nakit"
                R.id.radio_credit_card -> "Kredi Kartı"
                else -> null
            }
        }

        binding.btnContinue.setOnClickListener {
            if (selectedPaymentMethod == null) {
                Toast.makeText(requireContext(), "Lütfen bir ödeme yöntemi seçin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "Ödeme Yöntemi Seçildi: $selectedPaymentMethod")

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
                        orderViewModel.onPaymentSuccess(order) //firestora siparişi kaydettik

                        val bundle = Bundle()
                        bundle.putParcelable("latest_order", order)
                        //latesorder ı => cardpayment ve orderconfirmation a taşıdık

                        if (selectedPaymentMethod == "Kredi Kartı") {
                            findNavController().navigate(R.id.action_paymentSelectionFragment_to_cardPaymentFragment,bundle)
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
    private val TAG = "PaymentSelectionFragment" //sor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Ödeme Yöntemi Seçme
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedPaymentMethod = when (checkedId) {
                R.id.radio_cash -> "Nakit"
                R.id.radio_credit_card -> "Kredi Kartı"
                else -> null
            }
        }

        //Devam Et Butonu-> Ödeme sürecini başlatır
        binding.btnContinue.setOnClickListener {
            if (selectedPaymentMethod == null) {
                Toast.makeText(requireContext(), "Lütfen bir ödeme yöntemi seçin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "Ödeme Yöntemi Seçildi: $selectedPaymentMethod")

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val userId = currentUser.uid

                //Sepet verisini al ve sipariş oluştur
                cartViewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
                    if (cartItems.isNotEmpty()) {
                        val totalAmount = cartItems.sumOf { (it.price ?: 0.0) * (it.count ?: 0) }

                        val order = Order(
                            id = UUID.randomUUID().toString(),
                            userId = userId,
                            items = cartItems,  // SEPETTEKİ ÜRÜNLERİ KULLAN
                            totalAmount = totalAmount,
                            timestamp = System.currentTimeMillis()
                        )

                        Log.d(TAG, "Sipariş oluşturuldu: $order")
                        orderViewModel.onPaymentSuccess(order) //Siparişi Firestore’a kaydettik

                        // Ödeme Tamamlandıktan Sonra Kullanıcıyı Yönlendir
                        if (selectedPaymentMethod == "Kredi Kartı") {
                            findNavController().navigate(R.id.action_paymentSelectionFragment_to_cardPaymentFragment)
                        } else {
                            findNavController().navigate(R.id.action_paymentSelectionFragment_to_orderConfirmationFragment)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Sepetiniz boş!", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Log.e(TAG, "Kullanıcı giriş yapmamış, sipariş kaydedilemiyor!")
                Toast.makeText(requireContext(), "Giriş yapmanız gerekiyor!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


 */




/*
package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentPaymentSelectionBinding
import com.farukayata.e_commerce2.ui.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentSelectionFragment : Fragment() {

    private var _binding: FragmentPaymentSelectionBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel by viewModels<ProfileViewModel>()
    private var selectedPaymentMethod: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedPaymentMethod = when (checkedId) {
                R.id.radio_cash -> "Nakit"
                R.id.radio_credit_card -> "Kredi Kartı"
                else -> null
            }
        }

        binding.btnContinue.setOnClickListener {
            profileViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
                if (userProfile.address.isNullOrEmpty() || userProfile.phoneNumber.isNullOrEmpty() ||
                    userProfile.firstName.isNullOrEmpty() || userProfile.lastName.isNullOrEmpty()) {

                    Toast.makeText(requireContext(), "Eksik bilgileriniz var. Lütfen profilinizi tamamlayın.", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_paymentSelectionFragment_to_profileFragment)
                } else {
                    if (selectedPaymentMethod == "Kredi Kartı") {
                        findNavController().navigate(R.id.action_paymentSelectionFragment_to_cardPaymentFragment)
                    } else {
                        findNavController().navigate(R.id.action_paymentSelectionFragment_to_orderConfirmationFragment)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


 */