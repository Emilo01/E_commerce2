package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.farukayata.e_commerce2.databinding.FragmentCouponsBinding
import com.farukayata.e_commerce2.ui.adapter.CouponAdapter
import com.farukayata.e_commerce2.ui.viewmodel.CouponViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CouponsFragment : Fragment() {

    private lateinit var binding: FragmentCouponsBinding
    private val couponViewModel: CouponViewModel by viewModels()
    private lateinit var couponAdapter: CouponAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCouponsBinding.inflate(inflater, container, false)

        // kuponları almak için viewModel fonksiyonunu çağırıyoruz
        couponViewModel.fetchUserCoupons()

        couponViewModel.coupons.observe(viewLifecycleOwner) { coupons ->
            couponAdapter = CouponAdapter(coupons) { coupon ->
                if (couponViewModel.isCouponStillValid(coupon.issuedDate)) {
                    //Toast.makeText(requireContext(), "Kupon Kodu: ${coupon.code.orEmpty()}", Toast.LENGTH_SHORT).show()

                    //Null
                    coupon.code?.let { code ->
                        couponViewModel.applyCoupon(code, emptyList())
                    }

                } else {
                    //Toast.makeText(requireContext(), "Bu kuponun süresi dolmuştur!", Toast.LENGTH_SHORT).show()
                }
            }
            binding.recyclerViewCoupons.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewCoupons.adapter = couponAdapter
        }

        return binding.root
    }
}