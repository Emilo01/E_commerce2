package com.farukayata.e_commerce2.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentSplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment() {

    private lateinit var binding: FragmentSplashScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)

        Handler(Looper.getMainLooper()).postDelayed({
            when {
                isUserLoggedIn() -> {
                    // Kullanıcı giriş yaptıysa doğrudan ana sayfaya git
                    findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
                }
//                onBoardingFinished() -> {
//                    // Onboarding tamamlandıysa login ekranına yönlendir
//                    findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)
//                }
                else -> {
                    // Onboarding tamamlanmadıysa onboarding ekranına yönlendir
                    findNavController().navigate(R.id.action_splashScreenFragment_to_viewPagerFragment)
                }
            }
        }, 3000) // 3 saniye bekle

        return binding.root
    }

    private fun onBoardingFinished(): Boolean {
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("Finished", false)
    }
    // Kullanıcının giriş yapıp yapmadığını kontrol eden fonksiyon
    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences =
            requireActivity().getSharedPreferences("userSession", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
}
