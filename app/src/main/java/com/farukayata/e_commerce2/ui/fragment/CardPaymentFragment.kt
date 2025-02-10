package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentCardPaymentBinding
import com.farukayata.e_commerce2.model.Order
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardPaymentFragment : Fragment() {

    private var _binding: FragmentCardPaymentBinding? = null
    private val binding get() = _binding!!
    private var latestOrder: Order? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        latestOrder = arguments?.getParcelable("latest_order") //latestOrder alındı

        binding.btnPayNow.setOnClickListener {
            val cardNumber = binding.etCardNumber.text.toString()
            val expiryMonth = binding.etExpiryMonth.text.toString()
            val expiryYear = binding.etExpiryYear.text.toString()
            val cvv = binding.etCVV.text.toString()
            val cardHolder = binding.etCardHolder.text.toString()

            if (validateCardInfo(cardNumber, expiryMonth, expiryYear, cvv, cardHolder)) {
                val bundle = Bundle()
                //latesorderı paymentselectiondan aldık ve ordercpfirmation a gönderdik
                bundle.putParcelable("latest_order", latestOrder)
                findNavController().navigate(R.id.action_cardPaymentFragment_to_orderConfirmationFragment,bundle)
            } else {
                Toast.makeText(requireContext(), "Lütfen geçerli bir kart bilgisi girin!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateCardInfo(cardNumber: String, expiryMonth: String, expiryYear: String, cvv: String, cardHolder: String): Boolean {
        return cardNumber.length == 16 && expiryMonth.toIntOrNull() in 1..12 && expiryYear.length == 4 && cvv.length == 3 && cardHolder.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
