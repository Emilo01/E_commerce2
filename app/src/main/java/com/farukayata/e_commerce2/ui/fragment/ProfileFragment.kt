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
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentProfileBinding
import com.farukayata.e_commerce2.ui.adapter.CouponAdapter
import com.farukayata.e_commerce2.ui.viewmodel.CouponViewModel
import com.farukayata.e_commerce2.ui.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private val couponViewModel: CouponViewModel by viewModels()

    private lateinit var couponAdapter: CouponAdapter

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


        //aşası gerekmeye bilir
        // Kuponları al
        couponViewModel.fetchUserCoupons()

        couponViewModel.coupons.observe(viewLifecycleOwner) { coupons ->
            // Kuponları RecyclerView'da göster
            couponAdapter = CouponAdapter(coupons) { coupon ->
                // Uygulama işlemi, kuponun seçilmesi
                Toast.makeText(requireContext(), "Kupon Kodu: ${coupon.code}", Toast.LENGTH_SHORT).show()
            }
            // RecyclerView'u bağla
            //binding.recyclerViewCoupons.layoutManager = LinearLayoutManager(requireContext())
            //binding.recyclerViewCoupons.adapter = couponAdapter
        }

        //MyAccount Butonuna Tıklanınca Profil Detayına Git
        binding.buttonMyAccount.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileDetailFragment)

            //kullanıcı profil detailden profil sayfasına dönünce veriler nında gelcek
            viewModel.loadUserProfile()
        }

        binding.buttonMyCoupons.setOnClickListener {
            // Kuponlar sayfasına yönlendiriyoruz
            findNavController().navigate(R.id.action_profileFragment_to_couponsFragment)
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
