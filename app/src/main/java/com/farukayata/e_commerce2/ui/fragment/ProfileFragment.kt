package com.farukayata.e_commerce2.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

        viewModel.userEmail.observe(viewLifecycleOwner) { email ->
            binding.textViewUserEmail.text = email
        }


        //aşağısı gerekmeye bilir
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
        binding.root.findViewById<View>(R.id.buttonMyAccount).setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileDetailFragment)

            //kullanıcı profil detailden profil sayfasına dönünce veriler nında gelcek
            viewModel.loadUserProfile()
        }

        binding.root.findViewById<View>(R.id.buttonMyCoupons).setOnClickListener {
            // Kuponlar sayfasına yönlendiriyoruz
            findNavController().navigate(R.id.action_profileFragment_to_couponsFragment)
        }

        // Siparişlerim butonuna tıklanınca OrdersFragment'a geçiş
        binding.root.findViewById<View>(R.id.buttonMyOrders).setOnClickListener {
            try {
                Log.d("ProfileFragment", "Navigating to OrdersFragment")
                findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Error navigating to OrdersFragment: ${e.localizedMessage}")
            }
        }

        binding.buttonLogout.setOnClickListener {
            showLogoutDialog()
            //çıkış yap dediğindeki popup penceresi
        }
        //viewmodeldeki logout eventtimizi kontrol ediyoruz
        viewModel.logoutEvent.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) {
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            }
        }


        return binding.root
    }
//    private fun showLogoutDialog() {
//        AlertDialog.Builder(requireContext())
//            .setTitle("Çıkış Yap")
//            .setMessage("Hesabınızdan çıkış yapmak istediğinize emin misiniz?")
//            .setPositiveButton("Evet") { _, _ ->
//                viewModel.logout()
//            //düz logout
//            }
//            .setNegativeButton("İptal", null)
//            .show()
//    }

    private fun showLogoutDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_logout_dialog, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        // Butonları bul
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnLogout = dialogView.findViewById<Button>(R.id.btnLogout)

        // Çıkış işlemi
        btnLogout.setOnClickListener {
            viewModel.logout() //ViewModel'den çıkış işlemini çağırır
            dialog.dismiss()
        }

        // İptal işlemi
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        // Arka planı şeffaf yap ve göster
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
}
