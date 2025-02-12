package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentProfileBinding
import com.farukayata.e_commerce2.ui.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        //Data Binding için gerekli
        binding.viewModel = viewModel
        // Direkt bağlanıyor.

        // Kullanıcı Bilgilerini Gözlemleme
        viewModel.userProfile.observe(viewLifecycleOwner) { user ->
            binding.textViewUserName.text = "${user.firstName} ${user.lastName}"
            Glide.with(this).load(user.profileImageUrl).into(binding.imageViewUserProfile)
        }

        //MyAccount Butonuna Tıklanınca Profil Detayına Git
        binding.buttonMyAccount.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileDetailFragment)
        }

        // Siparişlerim butonuna tıklanınca OrdersFragment'a geçiş
        binding.buttonMyOrders.setOnClickListener {
            try {
                Log.d("ProfileFragment", "Navigating to OrdersFragment")
                findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Error navigating to OrdersFragment: ${e.localizedMessage}")
            }
        }

        //Çıkış Butonu
        binding.buttonLogout.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        return binding.root
    }
}
