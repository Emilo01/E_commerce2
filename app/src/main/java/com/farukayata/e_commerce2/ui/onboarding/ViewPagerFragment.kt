package com.farukayata.e_commerce2.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentViewPagerBinding

class ViewPagerFragment : Fragment() {

    private lateinit var binding: FragmentViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewPagerBinding.inflate(inflater, container, false)

        binding.viewpager.adapter = ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                //ilk ekranda geri butonunu gizle diğerlerinde göster
                binding.previousOnBoardingButton.visibility = if (position == 0) View.GONE else View.VISIBLE

                //son ekranda next butonunu değiştir
                if (position == 2) {  //eğer son ekrandaysa
                    binding.firstOnBoardingButton.text = "Begin to Shoping"
                    binding.firstOnBoardingButton.setOnClickListener {
                        onBoardingFinished()
                        findNavController().navigate(R.id.action_viewPagerFragment_to_homeFragment)
                    }
                } else {
                    binding.firstOnBoardingButton.text = "Next"
                    binding.firstOnBoardingButton.setOnClickListener {
                        binding.viewpager.currentItem++
                    }
                }
            }
        })

        //previous butonu
        binding.previousOnBoardingButton.setOnClickListener {
            if (binding.viewpager.currentItem > 0) {
                binding.viewpager.currentItem--
            }
        }

        return binding.root
    }

    private fun onBoardingFinished() {
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("Finished", true).apply()
    }
}









/*
package com.farukayata.e_commerce2.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentViewPagerBinding

class ViewPagerFragment : Fragment() {

    private lateinit var binding: FragmentViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewPagerBinding.inflate(inflater, container, false)

        binding.viewpager.adapter = ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0, 1 -> binding.firstOnBoardingButton.setOnClickListener {
                        binding.viewpager.currentItem++
                    }
                    else -> binding.firstOnBoardingButton.setOnClickListener {
                        onBoardingFinished()
                        findNavController().navigate(R.id.action_viewPagerFragment_to_homeFragment)
                    }
                }
            }
        })

        return binding.root
    }

    private fun onBoardingFinished() {
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("Finished", true).apply()
    }
}


 */