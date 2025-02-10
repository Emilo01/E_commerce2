package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentOrderConfirmationBinding
import com.farukayata.e_commerce2.model.Order
import com.farukayata.e_commerce2.ui.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderConfirmationFragment : Fragment() {

    private var _binding: FragmentOrderConfirmationBinding? = null
    private val binding get() = _binding!!
    private var latestOrder: Order? = null
    private val cartViewModel: CartViewModel by viewModels()
    //sepeti temizleme fonksiyonunu burdan çekcez

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        latestOrder = arguments?.getParcelable("latest_order") //latestOrder ı aldık

        cartViewModel.clearCart() // Sepeti temizledik

        binding.btnBackToHome.setOnClickListener {
            findNavController().navigate(R.id.action_orderConfirmationFragment_to_homeFragment)
        }

        binding.btnViewReceipt.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("latest_order", latestOrder) //latestOrder'ı receiptFragmetta gönderdik
            findNavController().navigate(R.id.action_orderConfirmationFragment_to_receiptFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





/*
package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentOrderConfirmationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderConfirmationFragment : Fragment() {

    private var _binding: FragmentOrderConfirmationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackToHome.setOnClickListener {
            findNavController().navigate(R.id.action_orderConfirmationFragment_to_homeFragment)
        }

        binding.btnViewReceipt.setOnClickListener {
            findNavController().navigate(R.id.action_orderConfirmationFragment_to_receiptFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


 */